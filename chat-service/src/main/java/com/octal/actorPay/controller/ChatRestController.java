package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ChatDTO;
import com.octal.actorPay.dto.ChatRoomResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.ChatRoomFilter;
import com.octal.actorPay.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
public class ChatRestController extends PagedItemsController {

    @Autowired
    ChatService chatService;

    @PostMapping(value = "/list/chat/rooms")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestBody ChatRoomFilter filterRequest,
                                                             HttpServletRequest request) throws Exception {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<ChatRoomResponse> pageResult =  chatService.getAllChatRooms(pagedInfo, filterRequest);
        return new ResponseEntity<>(new ApiResponse("chat Rooms  :  ", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/chat/room/{orderId}")
    public ResponseEntity<ApiResponse> getChatRoomByOrderId(@PathVariable("orderId") String orderId, HttpServletRequest request) throws Exception {
        ChatRoomResponse response =  chatService.getChatRoomByOrderId(orderId);
        return new ResponseEntity<>(new ApiResponse("chat room details ", response,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/chats/{roomId}")
    public ResponseEntity<ApiResponse> getChatsByOrderId(@PathVariable("roomId") String roomId,
                                                         @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                         HttpServletRequest request) throws Exception {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<ChatDTO> pageResult = chatService.getChatsByRoomId(roomId, pagedInfo);
        return new ResponseEntity<>(new ApiResponse("chats ", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/chat/deactivate/{orderId}")
    public ResponseEntity<ApiResponse> deactivateChatRommyOrderId(@PathVariable("orderId") String orderId, HttpServletRequest request) throws Exception {
        chatService.deactivateChatRoom(orderId);
        return new ResponseEntity<>(new ApiResponse("chat room deactivated successfully ", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}
