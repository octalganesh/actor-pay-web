package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface CrudOperation<E, T> {

    Boolean delete(@NotBlank String id);

    T read(String id, String actor);

    List<T> list(String actor);

    @NotBlank String create(@NotNull @Valid T entity, String actor);

    void update(@NotBlank String id, @NotNull @Valid T entity);




}
