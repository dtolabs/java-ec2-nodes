#!/usr/bin/perl
####
# simple CGI script produces nodes.xml by exec'ing the ec2-rundeck-node-generator jar
####

my $basedir="java-ec2-nodes";

###
# location of AWS credentials
###
my $awscreds="$basedir/AwsCredentials.properties";

###
# location of ec2-rundeck-node-generator.jar
###
my $genjar="$basedir/ec2-rundeck-node-generator-0.1.jar";

###
# location of mapping properties (optional)
# If not specified, or value of "-", then defaults are used
###
# my $genprops="$basedir/src/main/resources/simplemapping.properties";
my $genprops="-";

#execute ec2 generator
print "Content-Type: text/xml\n\n";

exec 'java' ,"-jar", $genjar, $awscreds, $genprops;



