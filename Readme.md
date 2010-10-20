EC2 integration to generate rundeck resources.xml file
============

The directory `ec2-rundeck-node-generator` contains a Java project that can be used to generate a resources.xml file which includes node definitions for all nodes described by the AWS EC2 API.

The `cgi` directory contains a simple CGI script to execute the node generator and return the XML.

Build
----

Using Ant:

    $ cd ec2-rundeck-node-generator
    $ ant

Using included Gradle wrapper:

    $ cd ec2-rundeck-node-generator
    $ ./gradlew
    
Produces: target/dist/ec2-rundeck-node-generator-0.1.jar

You can then execute the jar in-place as shown below, or you can relocate the entire "dist directory.

See the ec2-rundeck-node-generator/Readme.md for information on usage.

Using the CGI
----------

Copy the scripts/generatenodes.cgi script to your web server, make sure Apache is configured to execute .cgi files and that the file is executable.

Modify the "$awscreds" variable to be the path to a credentials properties file.  This file should contain:

    accessKey=<your access key>
    secretKey=<your secret key>

You can get your AWS access/secret key from this page: <http://aws.amazon.com/security-credentials>

Modify the "$genjar" variable to be the path to the ec2-rundeck-node-generator.jar file.

Finally, try to view the generatenodes.cgi through your webserver.

You can also specify a "mapping.properties" file to use to configure the way properties are set on the generated Nodes.  Set the "$genprops" variable to be the path to this file.  See the ec2-rundeck-node-generator/Readme.md for more information.