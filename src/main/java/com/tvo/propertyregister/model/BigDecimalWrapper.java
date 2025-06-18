package com.tvo.propertyregister.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class BigDecimalWrapper implements Serializable {
    private BigDecimal value;

    public BigDecimalWrapper(BigDecimal value){
        this.value = value;
    }

    public BigDecimal getValue() {
        return this.value;
    }
}
