

/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.UEMS;

/**
 * Created by sunny on 18-3-5.
 */

public class ElectType {
    private String typeName, typeId;

    public ElectType(String typeName, String typeId) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}
