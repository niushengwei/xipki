/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2013 - 2016 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 *
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

package org.xipki.commons.security.impl.p11.iaik;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xipki.commons.common.util.LogUtil;
import org.xipki.commons.common.util.ParamUtil;
import org.xipki.commons.security.api.p11.AbstractP11CryptServiceFactory;
import org.xipki.commons.security.api.p11.P11Module;
import org.xipki.commons.security.api.p11.P11ModuleConf;
import org.xipki.commons.security.api.p11.P11TokenException;

import iaik.pkcs.pkcs11.DefaultInitializeArgs;
import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;

/**
 * @author Lijun Liao
 * @since 2.0.0
 */

public class IaikP11CryptServiceFactory extends AbstractP11CryptServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(IaikP11CryptServiceFactory.class);

    private final Map<String, IaikP11Module> modules = new HashMap<>();

    @Override
    public synchronized void shutdown() {
        for (String pk11Lib : modules.keySet()) {
            try {
                modules.get(pk11Lib).close();
            } catch (Throwable th) {
                LOG.error("could not close PKCS11 Module " + pk11Lib + ":" + th.getMessage(),
                        th);
            }
        }
        modules.clear();
    }

    @Override
    protected synchronized P11Module getModule(
            final P11ModuleConf moduleConf)
    throws P11TokenException {
        ParamUtil.requireNonNull("moduleConf", moduleConf);
        IaikP11Module extModule = modules.get(moduleConf.getName());
        if (extModule != null) {
            return extModule;
        }

        Module module;

        try {
            module = Module.getInstance(moduleConf.getNativeLibrary());
        } catch (IOException ex) {
            final String msg = "could not load the PKCS#11 module " + moduleConf.getName();
            LOG.error(LogUtil.getErrorLog(msg), ex.getClass().getName(), ex.getMessage());
            LOG.debug(msg, ex);
            throw new P11TokenException(msg, ex);
        }

        try {
            module.initialize(new DefaultInitializeArgs());
        } catch (PKCS11Exception ex) {
            if (ex.getErrorCode() != PKCS11Constants.CKR_CRYPTOKI_ALREADY_INITIALIZED) {
                final String msg = "PKCS11Exception";
                LOG.error(LogUtil.getErrorLog(msg), ex.getClass().getName(), ex.getMessage());
                LOG.debug(msg, ex);
                close(moduleConf.getName(), module);
                throw new P11TokenException(ex.getMessage(), ex);
            } else {
                LOG.info("PKCS#11 module already initialized");
                if (LOG.isInfoEnabled()) {
                    try {
                        LOG.info("pkcs11.getInfo():\n{}", module.getInfo());
                    } catch (TokenException e2) {
                        LOG.debug("module.getInfo()", e2);
                    }
                }
            }
        } catch (Throwable th) {
            final String msg = "unexpected Exception: ";
            LOG.error(LogUtil.getErrorLog(msg), th.getClass().getName(), th.getMessage());
            LOG.debug(msg, th);
            close(moduleConf.getName(), module);
            throw new P11TokenException(th.getMessage());
        }

        extModule = new IaikP11Module(module, moduleConf);
        modules.put(moduleConf.getName(), extModule);

        return extModule;
    }

    private static void close(
            final String modulePath,
            final Module module) {
        if (module == null) {
            return;
        }

        LOG.info("close", "close pkcs11 module: {}", modulePath);
        try {
            module.finalize(null);
        } catch (Throwable th) {
            final String message = "could not clonse module " + modulePath;
            LOG.error(LogUtil.getErrorLog(message), th.getClass().getName(), th.getMessage());
            LOG.debug(message, th);
        }
    }

}