package com.babel.venus.util;

public interface InitPayoffUtil {
    void initPayOffMap(Long platInfoId, Long lotteryId);

    Long getRealPlatId(Long platInfoId, Integer acType);
}
