Build
------

Using Ant:

    $ cd ec2-rundeck-node-generator
    $ ant

Using included Gradle wrapper:

    $ cd ec2-rundeck-node-generator
    $ ./gradlew
    
Produces: target/dist/ec2-rundeck-node-generator-0.1.jar

You can then execute the jar in-place as shown below, or you can relocate the entire "dist" directory.

Usage
--------

    java -jar ec2-rundeck-node-generator.jar <mapping.properties> <awscredentials.properties> [output.xml]

By default it produces the XML output on STDOUT.  if `output.xml` file is specified, the
XML content will be stored in the given file.

`mapping.properties` is a properties file specifying how to map EC2 Instance metadata to the Node metadata used by Rundeck.  (See below).

`awscredentials.properties` specifies the Access Key and Secret Key for API access to AWS.

    accessKey=<your access key>
    secretKey=<your secret key>

Mapping EC2 Instances to Rundeck Nodes
=================

Rundeck node definitions specify mainly the pertinent data for connecting to and organizing the Nodes.  EC2 Instances have metadata that can be mapped onto the fields used for Rundeck Nodes.

Rundeck nodes have the following metadata fields:

* `name` - unique identifier
* `hostname` - IP address/hostname to connect to the node
* `username` - SSH username to connect to the node
* `description` - textual description
* `osName` - OS name
* `osFamily` - OS family: unix, windows, cygwin.
* `osArch` - OS architecture
* `osVersion` - OS version
* `tags` - set of labels for organization
* `editUrl` - URL to edit the definition of this node object
* `remoteUrl` - URL to edit the definition of this node object using Rundeck-specific integration

In addition, Nodes can have arbitrary attribute values.

EC2 Instance Field Selectors
-----------------

EC2 Instances have a set of metadata that can be mapped to any of the Rundeck node fields, or to Settings or tags for the node.

EC2 fields:

* amiLaunchIndex
* architecture
* clientToken
* imageId
* instanceId
* instanceLifecycle
* instanceType
* kernelId
* keyName
* launchTime
* license
* platform
* privateDnsName
* privateIpAddress
* publicDnsName
* publicIpAddress
* ramdiskId
* rootDeviceName
* rootDeviceType
* spotInstanceRequestId
* state
* stateReason
* stateTransitionReason
* subnetId
* virtualizationType
* vpcId
* `tags/*`

EC2 Instances can also have "Tags" which are key/value pairs attached to the Instance.  A common Tag is "Name" which could be a unique identifier for the Instance, making it a useful mapping to the Node's name field.  Note that EC2 Tags differ from Rundeck Node tags: Rundeck tags are simple string labels and are not key/value pairs.

Defining the mapping
---------------

Define the mapping in a .properties formatted file.  The mapping consists of defining either a selector or a default for
the desired Node fields.  The "name" and "hostname" fields are required.

For purposes of this mapping file, a `field selector` is either:

* An EC2 fieldname, or dot-separated field names
* "tags/" followed by a Tag name, e.g. "tags/My Tag"
* "tags/*" for use by the `settings.selector` mapping

Selectors use the Apache [BeanUtils](http://commons.apache.org/beanutils/) to extract a property value from the AWS API
[Instance class](http://docs.amazonwebservices.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/Instance.html).
This means you can use dot-separated fieldnames to traverse the object graph.
E.g. "state.name" to specify the "name" field of the State property of the Instance.

format:

    # define a selector for "property":
    <property>.selector=<field selector>
    # define a default value for "property":
    <property>.default=<default value>
    # define an attribute with a selector
    attribute.<name>.selector=<field selector>
    # define an attribute default value
    attribute.<name>.default=<default value>
    # Special attributes selector to map all Tags to attributes
    attributes.selector=tags/*
    # The value for the tags selector will be treated as a comma-separated list of strings
    tags.selector=<field selector>
    # Define a single tag <name> which will be set if and only if the selector result is not empty
    tag.<name>.selector=<field selector>
    # Define a single tag <name> which will be set if the selector result equals the <value>
    tag.<name>.selector=<field selector>=<value>

When defining field selector for the `tags` node property, the string value selected (if any) will
be treated as a comma-separated list of strings to use as node tags.  You could, for example, set a custom EC2 Tag on
an instance to contain this list of tags, in this example from the simplemapping.properties file:

    tags.selector=tags/Rundeck-Tags

So creating the "Rundeck-Tags" Tag on the EC2 Instance with a value of "alpha, beta" will result in the node having
those two node tags.

You can also use the <field selector>=<value> feature to set a tag only if the field selector has a certain value.
