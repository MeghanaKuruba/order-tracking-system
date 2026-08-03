package com.ordertracking.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeoapifyResponse {

    private List<GeoapifyProperties> results;

}