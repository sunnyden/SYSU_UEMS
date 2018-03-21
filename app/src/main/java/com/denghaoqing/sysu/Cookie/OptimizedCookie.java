/*
 *  Copyright (C) 2013 - 2018, Haoqing Deng <dhq.sunny@gmail.com>
 *
 *  This file is part of the SYSU UEMS.
 *
 *  SYSU UEMS is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SYSU UEMS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SYSU UEMS; see the file COPYING. If not, see
 *  <http://www.gnu.org/licenses/>.
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
