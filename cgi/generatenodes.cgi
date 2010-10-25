#!/usr/bin/perl
####
# simple CGI script produces nodes.xml by exec'ing the ec2-rundeck-node-generator jar
####
use CGI qw/:cgi :cgi-lib/;

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

###
# specify query parameters, in form a=b.  e.g. "tag:mytag=value"
###
my @params=qw();


###
# if set to 1, allow http query parameters to be used as filters
###
my $allowqparams=1;

if($allowqparams){
    %vars=Vars();
    #use http query parameters as filters
    foreach my $k (keys %vars){
        push @params,"$k=".$vars{$k};
    }
}

#execute ec2 generator
print header(-type=>'text/xml');
#print "Content-Type: text/xml\n\n";

exec 'java' ,"-jar", $genjar, $awscreds, $genprops,"-",@params;



