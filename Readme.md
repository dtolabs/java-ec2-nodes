Deprecation Notice
============

**NOTICE:** This ec2 nodes provider is for RunDeck 1.3 and earlier.  It is now deprecated in favor of the plugin mechanism (to be) introduced in 
RunDeck 1.4.  

A new Github project provides a plugin offering the ec2 node functionality for RunDeck 1.4:

* https://github.com/gschueler/rundeck-ec2-nodes-plugin

Please use the new plugin for RunDeck 1.4+ and file any issues under the Github Issues for that project.

Thank you,

 - Greg Schueler, 10/18/2011

EC2 integration to generate rundeck resources.xml file
============

The directory `ec2-rundeck-node-generator` contains a Java project that can be used to generate a resources.xml file which includes node definitions for all nodes described by the AWS EC2 API.

The `cgi` directory contains a simple CGI script to execute the node generator and return the XML.

Build
----

Using included Gradle wrapper:

    $ ./gradlew
    
Produces: target/java-ec2-nodes-0.2-bin.zip

Unzip this file, e.g at $RDECK_BASE/java-ec2-nodes, and use the enclosed cgi file.

See the ec2-rundeck-node-generator/Readme.md file for information on usage of the jar file.

Using the CGI
----------

Copy the generatenodes.cgi script to your web server, make sure Apache is configured to execute .cgi files and that the file is executable.

Modify the "$awscreds" variable to be the path to a credentials properties file.  This file should contain:

    accessKey=<your access key>
    secretKey=<your secret key>

You can get your AWS access/secret key from this page: <http://aws.amazon.com/security-credentials>

Modify the "$genjar" variable to be the path to the ec2-rundeck-node-generator.jar file.

You can also specify a "mapping.properties" file to use to configure the way properties are set on the generated Nodes.  Set the "$genprops" variable to be the path to this file.  See the ec2-rundeck-node-generator/Readme.md for more information.

Finally, try to view the generatenodes.cgi through your webserver.  For example, using curl:

    $ curl http://myserver/generatenodes.cgi

Output:

    <?xml version="1.0" encoding="UTF-8"?>

    <project>
      <node name="Example4" type="Node" description="Ec2 node instance" hostname="ec2-127-0-0-205.compute-1.amazonaws.com" osArch="x86_64" osFamily="unix" osName="Linux" osVersion="" username="rundeck-user" editUrl="" remoteUrl="" privateIpAddress="127.0.29.73" privateDnsName="ip-127-0-29-73.ec2.internal"/>
      <node name="Example1" type="Node" description="Ec2 node instance" hostname="ec2-127-0-0-73.compute-1.amazonaws.com" osArch="x86_64" osFamily="unix" osName="Linux" osVersion="" username="rundeck-user" editUrl="" remoteUrl="" privateIpAddress="127.1.54.85" privateDnsName="ip-127-1-54-85.ec2.internal"/>
      <node name="Example5" type="Node" description="Ec2 node instance" hostname="ec2-127-0-0-206.compute-1.amazonaws.com" osArch="x86_64" osFamily="unix" osName="Linux" osVersion="" username="rundeck-user" editUrl="" remoteUrl="" privateIpAddress="127.0.29.44" privateDnsName="ip-127-0-29-44.ec2.internal"/>
      <node name="Example3" type="Node" description="Ec2 node instance" hostname="ec2-127-0-0-190.compute-1.amazonaws.com" osArch="x86_64" osFamily="unix" osName="Linux" osVersion="" username="rundeck-user" editUrl="" remoteUrl="" privateIpAddress="127.0.11.34" privateDnsName="ip-127-0-11-34.ec2.internal"/>
      <node name="Example2" type="Node" description="Ec2 node instance" hostname="ec2-127-0-0-178.compute-1.amazonaws.com" osArch="x86_64" osFamily="unix" osName="Linux" osVersion="" username="rundeck-user" editUrl="" remoteUrl="" privateIpAddress="127.2.5.19" privateDnsName="ip-127-2-5-19.ec2.internal"/>
    </project>

Release Notes
-----

* 0.2 - Updated to support Rundeck 1.2+ resources format
* 0.1 - initial release