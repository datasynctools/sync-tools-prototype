/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author  Upendra Jariya
 * @sponsor Douglas Johnson
 * @copyright datasync.tools
 * @version 1.0
 * @since   20-Nov-2014
 */

package tools.datasync.basic.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class ObjectMapperFactory {

    private static ObjectMapper bean = null;

    public static synchronized ObjectMapper getInstance() {

	if (bean == null) {
	    bean = new ObjectMapper();

	    bean.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	    bean.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

	    // http://stackoverflow.com/questions/6591388/configure-jackson-to-deserialize-single-quoted-invalid-json
	    bean.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

	    bean.disable(SerializationConfig.Feature.INDENT_OUTPUT);

	    bean.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

	}

	return bean;
    }

}
