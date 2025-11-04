package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.resp.SaxoStatisShareTradesDTO;

import java.util.Date;
import java.util.Map;

public interface SaxoStatisService {
    Map<Long,SaxoStatisShareTradesDTO> statisShareTreadesExecute(Date nowDate);
}
