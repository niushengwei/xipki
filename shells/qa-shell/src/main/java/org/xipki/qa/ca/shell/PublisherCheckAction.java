/*
 *
 * Copyright (c) 2013 - 2018 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xipki.qa.ca.shell;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.xipki.ca.mgmt.api.PublisherEntry;
import org.xipki.ca.mgmt.shell.PublisherUpdateAction;
import org.xipki.shell.CmdFailure;

/**
 * TODO.
 * @author Lijun Liao
 * @since 2.0.0
 */

@Command(scope = "caqa", name = "publisher-check",
    description = "check information of publishers (QA)")
@Service
public class PublisherCheckAction extends PublisherUpdateAction {

  @Override
  protected Object execute0() throws Exception {
    println("checking publisher " + name);

    PublisherEntry cp = caManager.getPublisher(name);
    if (cp == null) {
      throw new CmdFailure("publisher named '" + name + "' is not configured");
    }

    if (cp.getType() != null) {
      MgmtQaShellUtil.assertTypeEquals("type", type, cp.getType());
    }

    if (cp.getConf() != null) {
      MgmtQaShellUtil.assertEquals("signer conf", conf, cp.getConf());
    }

    println(" checked publisher " + name);
    return null;
  }

}
