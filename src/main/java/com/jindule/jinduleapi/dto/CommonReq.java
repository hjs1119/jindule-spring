package com.jindule.jinduleapi.dto;

import lombok.Data;

@Data
public class CommonReq {
    String region;
    String methodType;
    String path;
    String params;
}
