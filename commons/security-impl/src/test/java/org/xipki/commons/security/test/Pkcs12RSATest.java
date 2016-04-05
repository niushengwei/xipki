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

package org.xipki.commons.security.test;

import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;
import org.xipki.commons.common.ConfPairs;
import org.xipki.commons.security.api.ConcurrentContentSigner;
import org.xipki.commons.security.api.util.X509Util;
import org.xipki.commons.security.impl.SecurityFactoryImpl;

/**
 * @author Lijun Liao
 * @since 2.0.0
 */
// CHECKSTYLE:SKIP
public abstract class Pkcs12RSATest {

    private static final SecurityFactoryImpl SECURITY_FACTORY = new SecurityFactoryImpl();

    private ConcurrentContentSigner signer;

    protected Pkcs12RSATest() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    protected abstract ASN1ObjectIdentifier getSignatureAlgorithm();

    protected String getPkcs12File() {
        return "src/test/resources/C.TSL.SIG1.p12";
    }

    protected String getCertificateFile() {
        return "src/test/resources/C.TSL.SIG1.der";
    }

    protected String getPassword() {
        return "1234";
    }

    private String getSignerConf() {
        ConfPairs conf = new ConfPairs("password", getPassword());
        conf.putPair("algo", getSignatureAlgorithm().getId());
        conf.putPair("keystore", "file:" + getPkcs12File());
        return conf.getEncoded();
    }

    private ConcurrentContentSigner getSigner()
    throws Exception {
        if (signer == null) {
            String certFile = getCertificateFile();
            X509Certificate cert = X509Util.parseCert(certFile);

            String signerConf = getSignerConf();
            signer = SECURITY_FACTORY.createSigner("PKCS12", signerConf, cert);
        }
        return signer;
    }

    @Test
    public void testSignAndVerify()
    throws Exception {
        byte[] data = new byte[1234];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i & 0xFF);
        }

        byte[] signatureValue = sign(data);
        boolean signatureValid = verify(data, signatureValue, getSigner().getCertificate());
        Assert.assertTrue("Signature invalid", signatureValid);
    }

    protected byte[] sign(
            final byte[] data)
    throws Exception {
        return getSigner().sign(data);
    }

    protected boolean verify(
            final byte[] data,
            final byte[] signatureValue,
            final X509Certificate cert)
    throws Exception {
        Signature signature = Signature.getInstance(getSignatureAlgorithm().getId());
        signature.initVerify(cert.getPublicKey());
        signature.update(data);
        return signature.verify(signatureValue);
    }

}