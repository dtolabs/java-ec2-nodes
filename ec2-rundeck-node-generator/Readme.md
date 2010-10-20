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

In addition, Nodes can have "Setting" values.  A Setting is a named entity with a string value.  Since Settings can be shared between Nodes, specific Settings must be named uniquely for each Node.  When defined, the name for each Setting will be made unique by prepending the name of the associated Node.

EC2 Instances have a set of metadata that can be mapped to any of the Rundeck node fields, or to Settings for the node.

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

Define the mapping in a .properties formatted file.  The mapping consists of defining either a selector or a default for the desired Node fields.  The "name" and "hostname" fields are required.

format:

    # define a selector for "property":
    <property>.selector=<field selector>
    # define a default value for "property":
    <property>.default=<default value>
    # define a Setting with a selector
    setting.<name>.selector=<field selector>
    # define a Setting default value
    setting.<name>.default=<default value>
    # Special settings selector to map all Tags to Settings
    settings.selector=tags/*
    
Selector ~ this is either an EC2 field name selector, or to specify a tag: "tags/<name>".  Selectors use the Apache [BeanUtils](http://commons.apache.org/beanutils/) to extract a property value from the AWS API [Instance class](http://docs.amazonwebservices.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/Instance.html).  This means you can use dot-separated fieldnames to traverse the object graph.  E.g. "state.name" to specify the "name" field of the State property of the Instance.

