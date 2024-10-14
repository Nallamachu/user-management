package com.msrts.hostel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private T data;
    private List<Error> errors;
    private Number currentPage;
    private Number totalItems;
    private Number totalPages;

    public Response(T data, List<Error> errors) {
        this.data = data;
        this.errors = errors;
    }
}
