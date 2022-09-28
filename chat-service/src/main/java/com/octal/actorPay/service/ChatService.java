package com.octal.actorPay.service;

import com.octal.actorPay.dto.AuthUserDTO;
import com.octal.actorPay.dto.ChatDTO;
import com.octal.actorPay.dto.ChatMessage;
import com.octal.actorPay.dto.ChatRoomResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.ChatRoomFilter;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.jwt.JwtTokenProvider;
import com.octal.actorPay.model.Chat;
import com.octal.actorPay.model.ChatRoom;
import com.octal.actorPay.repositories.ChatRepository;
import com.octal.actorPay.repositories.ChatRoomRepository;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAdminDetailsService adminDetailsService;
    private final CustomMerchantDetailsService merchantDetailsService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private SpecificationFactory<Chat> specificationFactory;

    @Autowired
    private SpecificationFactory<ChatRoom> chatRoomSpecificationFactory;

    public ChatService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository,
                       JwtTokenProvider jwtTokenProvider,
                       CustomAdminDetailsService adminDetailsService,
                       CustomMerchantDetailsService merchantDetailsService,
                       CustomUserDetailsService userDetailsService) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.adminDetailsService = adminDetailsService;
        this.merchantDetailsService = merchantDetailsService;
        this.userDetailsService = userDetailsService;
    }

    public void saveChat(String orderId, ChatMessage chatMessage) {
        ChatRoom chatRoom = chatRoomRepository.findByOrderId(orderId);
        Chat chat = new Chat();
        chat.setChatRoomId(chatRoom.getId());
        chat.setMessage(chatMessage.getContent());
        chat.setSenderName(chatMessage.getSender());
        chat.setCreatedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }

    public void saveChatRoom(String orderId, ChatMessage chatMessage) {
        ChatRoom chatRoom = chatRoomRepository.findByOrderId(orderId);
        if (chatRoom != null) {
            chatRoom.setActive(true);
            chatRoom.setUpdatedAt(LocalDateTime.now());
        } else {
            chatRoom = new ChatRoom();
            chatRoom.setOrderId(orderId);
            chatRoom.setUserName(chatMessage.getSender());
            chatRoom.setCreatedAt(LocalDateTime.now());
        }
        chatRoomRepository.save(chatRoom);
    }

    public void deactivateChatRoom(String orderId) {
        ChatRoom chatRoom = chatRoomRepository.findByOrderId(orderId);
        chatRoom.setActive(false);
        chatRoomRepository.save(chatRoom);
    }


    public AuthUserDTO resolveToken(String token) throws Exception {

        String resolvedToken = jwtTokenProvider.resolveToken(token);
        if (!Objects.isNull(token) && jwtTokenProvider.isTokenExpired(resolvedToken)) {
            Claims claims = jwtTokenProvider.getUsername(resolvedToken);
            List<String> scopes = claims.get("scopes", List.class);
            if (scopes.contains("ADMIN")) {
                return adminDetailsService.loadUserByUsername(claims.getSubject());
            } else if (scopes.contains("MERCHANT")) {
                return merchantDetailsService.loadUserByUsername(claims.getSubject());
            } else {
                return userDetailsService.loadUserByUsername(claims.getSubject());
            }
        } else {
            throw new Exception("Request not authorized");
        }
    }

    public ChatRoomResponse getChatRoomByOrderId(String orderId) {
        ChatRoomResponse response = new ChatRoomResponse();
        ChatRoom chatRoom = chatRoomRepository.findByOrderId(orderId);

        if (chatRoom == null) {
            throw new ActorPayException("Invalid order id : " + orderId);
        }
        response.setActive(chatRoom.isActive());
        response.setOrderId(chatRoom.getOrderId());
        response.setUserName(chatRoom.getUserName());
        response.setUpdatedAt(chatRoom.getUpdatedAt());
        response.setCreatedAt(chatRoom.getCreatedAt());
        return response;
    }

    public PageItem<ChatDTO> getChatsByRoomId(String roomId, PagedItemInfo pagedInfo) {
        List<ChatDTO> chatDTOList = new ArrayList<>();
        GenericSpecificationsBuilder<Chat> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Chat.class, pagedInfo);
        prepareChatSearchQuery(roomId, builder);
        Page<Chat> pageResult = chatRepository.findAll(builder.build(), pageRequest);
        for (Chat chat : pageResult) {
            ChatDTO dto = new ChatDTO();
            dto.setChatRoomId(chat.getChatRoomId());
            dto.setMessage(chat.getMessage());
            dto.setSenderName(chat.getSenderName());
            dto.setCreatedAt(chat.getCreatedAt());
            chatDTOList.add(dto);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), chatDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    private void prepareChatSearchQuery(String roomId, GenericSpecificationsBuilder<Chat> builder) {
        builder.with(specificationFactory.isEqual("chatRoomId", roomId));
    }

    public PageItem<ChatRoomResponse> getAllChatRooms(PagedItemInfo pagedInfo, ChatRoomFilter filterRequest) {
        List<ChatRoomResponse> chatDTOList = new ArrayList<>();
        GenericSpecificationsBuilder<ChatRoom> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(ChatRoom.class, pagedInfo);
        prepareChatRoomSearchQuery(filterRequest, builder);
        Page<ChatRoom> pageResult = chatRoomRepository.findAll(builder.build(), pageRequest);
        for (ChatRoom chatRoom : pageResult) {
            ChatRoomResponse dto = new ChatRoomResponse();
            dto.setUserName(chatRoom.getUserName());
            dto.setOrderId(chatRoom.getOrderId());
            dto.setCreatedAt(chatRoom.getCreatedAt());
            dto.setUpdatedAt(chatRoom.getUpdatedAt());
            chatDTOList.add(dto);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), chatDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    private void prepareChatRoomSearchQuery(ChatRoomFilter filterRequest, GenericSpecificationsBuilder<ChatRoom> builder) {

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getUserName())) {
            builder.with(chatRoomSpecificationFactory.isEqual("userName", filterRequest.getUserName()));
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getOrderId())) {
            builder.with(chatRoomSpecificationFactory.isEqual("orderId", filterRequest.getOrderId()));
        }

        if (filterRequest.getStartDate() != null) {
            builder.with(chatRoomSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }

        if (filterRequest.getEndDate() != null) {
            builder.with(chatRoomSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
    }
}
