package com.pivot.aham.api.server.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReceivedTransferResult implements Serializable{
    private List<ReceivedTransferItem> itemList;
}
