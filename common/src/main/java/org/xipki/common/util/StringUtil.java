/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 - 2015 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * THE AUTHOR LIJUN LIAO. LIJUN LIAO DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the XiPKI software without
 * disclosing the source code of your own applications.
 *
 * For more information, please contact Lijun Liao at this
 * address: lijun.liao@gmail.com
 */

package org.xipki.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Lijun Liao
 */

public class StringUtil
{
    public static List<String> split(
            final String str,
            final String delim)
    {
        if(str == null)
        {
            return null;
        }

        if(str.isEmpty())
        {
            return Collections.emptyList();
        }

        StringTokenizer st = new StringTokenizer(str, delim);
        List<String> ret = new ArrayList<>(st.countTokens());

        while(st.hasMoreTokens())
        {
            ret.add(st.nextToken());
        }

        return ret;
    }

    public static boolean isBlank(
            final String s)
    {
        return s == null || s.isEmpty();
    }

    public static boolean isNotBlank(
            final String s)
    {
        return s != null && s.isEmpty() == false;
    }

    public static Set<String> splitAsSet(
            final String str,
            final String delim)
    {
        if(str == null)
        {
            return null;
        }

        if(str.isEmpty())
        {
            return Collections.emptySet();
        }

        StringTokenizer st = new StringTokenizer(str, delim);
        Set<String> ret = new HashSet<>(st.countTokens());

        while(st.hasMoreTokens())
        {
            ret.add(st.nextToken());
        }

        return ret;
    }

    public static String collectionAsString(
            final Collection<String> set,
            final String delim)
    {
        if(set == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for(String m : set)
        {
            sb.append(m).append(delim);
        }
        int n = sb.length();
        if(n > 0)
        {
            sb.delete(n - delim.length(), n);
        }
        return sb.toString();
    }

    public static boolean startsWithIgnoreCase(
            final String s,
            final String prefix)
    {
        if(s.length() < prefix.length())
        {
            return false;
        }

        return prefix.equalsIgnoreCase(s.substring(0, prefix.length()));
    }
}