#!/usr/bin/env groovy

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
* testnodes.java
*
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Oct 18, 2010 4:30:49 PM
*
*/

stuff="test"

// Spitting out standard HTTP Header

println "Content-type: text/html\n"


// Using here-docs to generated HTML content
html = """
<html>
<head>
<title>Hello, Groovy</title>
</head>
<body background="/images/blue-dash.gif">
<h3>${stuff}</h3>
</body>
</html>
"""

// Spitting out standard Hello, Groovy
println html