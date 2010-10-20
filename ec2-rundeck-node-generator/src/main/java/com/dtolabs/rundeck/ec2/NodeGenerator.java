/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/*
* NodeGenerator.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Oct 18, 2010 7:03:37 PM
* 
*/
package com.dtolabs.rundeck.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.shared.resources.ResourceXMLConstants;
import com.dtolabs.shared.resources.ResourceXMLGenerator;
import org.apache.commons.beanutils.BeanUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NodeGenerator produces Rundeck node definitions in XML format from EC2 Instances 
 *
 * @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
 */
public class NodeGenerator {
    public static void main(final String[] args) throws IOException, GeneratorException {
        File outfile = null;

        //load generator mapping
        if (args.length < 1) {
            System.err.println("usage: <credentials.properties> [mapping.properties] [outfile]");
            System.exit(2);
        }

        final InputStream stream = new FileInputStream(args[0]);
        final AWSCredentials credentials = new PropertiesCredentials(stream);

        Properties mapping = new Properties();
        if (args.length > 1 && !"-".equals(args[1])) {
            mapping.load(new FileInputStream(args[1]));
        } else {
            mapping.load(NodeGenerator.class.getClassLoader().getResourceAsStream("simplemapping.properties"));
        }


        final ResourceXMLGenerator gen;
        if (args.length > 2 && !"-".equals(args[2])) {
            outfile = new File(args[2]);
            gen = new ResourceXMLGenerator(outfile);
        } else {
            //use stdout
            gen = new ResourceXMLGenerator(System.out);
        }


        AmazonEC2Client ec2 = new AmazonEC2Client(credentials);

        DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();

        for (final Reservation reservation : reservations) {
            instances.addAll(reservation.getInstances());
        }

        for (final Instance inst : instances) {
            gen.addNode(instanceToNode(inst, mapping));
        }
        gen.generate();
        //
        if (null != outfile) {
            System.out.println("XML Stored: " + outfile.getAbsolutePath());
        }
    }

    public static INodeEntry instanceToNode(final Instance inst, final Properties mapping) throws GeneratorException {
        String nameSel = mapping.getProperty("name.selector");
        String name = applySelector(inst, nameSel, mapping.getProperty("name.default"));
        String hostSel = mapping.getProperty("hostname.selector");
        String host = applySelector(inst, nameSel, mapping.getProperty("hostname.default"));
        NodeEntryImpl node = new NodeEntryImpl(host, name);
        node.setType("Node");
        String descSel = mapping.getProperty("description.selector");
        String desc = applySelector(inst, descSel, mapping.getProperty("description.default"));
        node.setDescription(desc);

        for (final String prop : ResourceXMLConstants.nodeProps) {
            final String value = applySelector(inst, mapping.getProperty(prop + ".selector"),
                mapping.getProperty(prop + ".default"));
            if (null != value) {
                try {
                    BeanUtils.setProperty(node, prop, value);
                } catch (Exception e) {
                    throw new GeneratorException(e);
                }
            }
        }

        Pattern settingPat = Pattern.compile("^setting\\.(.+?)\\.selector$");
        //evaluate setting selectors
        for (final Object o : mapping.keySet()) {
            String key = (String) o;
            String selector = mapping.getProperty(key);
            Matcher m = settingPat.matcher(key);
            if (m.matches()) {
                String setName = m.group(1);
                if (null == node.getSettings()) {
                    node.setSettings(new HashMap<String, String>());
                }
                final String value = applySelector(inst, selector, mapping.getProperty(
                    "setting." + setName + ".default"));
                if (null != value) {
                    //use nodename-settingname to make the setting unique to the node
                    node.getSettings().put(name + "-" + setName, value);
                }
            }
        }
        //evaluate single settings.selector=tags/* mapping
        if ("tags/*".equals(mapping.getProperty("settings.selector"))) {
            //iterate through instance tags and generate settings
            for (final Tag tag : inst.getTags()) {
                if (null == node.getSettings()) {
                    node.setSettings(new HashMap<String, String>());
                }
                node.getSettings().put(name + "-" + tag.getKey(), tag.getValue());
            }
        }

        return node;
    }

    public static String applySelector(Instance inst, String selector, String defaultValue) throws GeneratorException {
        if (null != selector && selector.startsWith("tags/")) {
            String tag = selector.substring("tags/".length());
            final List<Tag> tags = inst.getTags();
            for (final Tag tag1 : tags) {
                if (tag.equals(tag1.getKey())) {
                    return tag1.getValue();
                }
            }
        } else if (null != selector) {
            try {
                final String value = BeanUtils.getProperty(inst, selector);
                if(null!=value){
                    return value;
                }
            } catch (Exception e) {
                throw new GeneratorException(e);
            }
        }

        return defaultValue;
    }

    public static class GeneratorException extends Exception {
        public GeneratorException() {
        }

        public GeneratorException(String message) {
            super(message);
        }

        public GeneratorException(String message, Throwable cause) {
            super(message, cause);
        }

        public GeneratorException(Throwable cause) {
            super(cause);
        }
    }


    public void writeNodeElement(Writer out, Map<String, String> data) throws IOException {
        out.write("<node ");
        //write attribs


        out.write(">\n");


        //write content

        //write close
        out.write("</node>\n");
    }
}