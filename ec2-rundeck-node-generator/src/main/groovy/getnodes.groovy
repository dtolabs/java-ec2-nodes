#!/usr/bin/env groovy

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.services.ec2.model.Instance

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
* getnodes.java
*
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Oct 18, 2010 4:30:49 PM
*
*/


final InputStream stream = this.class.getResourceAsStream("AwsCredentials.properties")
if(null==stream){
    System.err.println("No file found: AwsCredentials.properties");
    System.exit(2)
}
AWSCredentials credentials = new PropertiesCredentials(
    stream);

ec2 = new AmazonEC2Client(credentials);

//DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
//System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
//        " Availability Zones.");

DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
List<Reservation> reservations = describeInstancesRequest.getReservations();
Set<Instance> instances = new HashSet<Instance>();

for (Reservation reservation : reservations) {
    instances.addAll(reservation.getInstances());
}

System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");

for (Instance inst: instances) {
    System.out.println("id: " + inst.getInstanceId())
    System.out.println("\thostname: " + inst.getPublicDnsName())
    System.out.println("\thostname: " + inst.getArchitecture())
    System.out.println("\tarch: " + inst.getArchitecture())
    System.out.println("\tplatform: " + inst.getPlatform())
    System.out.println("\ttags: " + inst.getTags())
    
}