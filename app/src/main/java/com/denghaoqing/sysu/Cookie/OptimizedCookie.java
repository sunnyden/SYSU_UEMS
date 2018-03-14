/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Cookie;

import java.util.Date;

import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by sunny on 18-2-26.
 */

public class OptimizedCookie implements Cookie {
    private String Domain;
    private String Path;
    private String Name;
    private String Value;

    public OptimizedCookie(String domain, String path, String name, String value) {
        this.Domain = domain;
        this.Path = path;
        this.Name = name;
        this.Value = value;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getCommentURL() {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean isSecure() {
        return true;
    }


    @Override
    public boolean isExpired(Date date) {
        return false;
    }

    @Override
    public Date getExpiryDate() {
        return null;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public int[] getPorts() {
        return new int[0];
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getValue() {
        return Value;
    }

    @Override
    public String getDomain() {
        return Domain;
    }

    @Override
    public String getPath() {
        return Path;
    }
}
