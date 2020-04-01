node {
	properties([
		// Below line sets "Discard Builds more than 5"
		buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 
		
		// Below line triggers this job every minute
		pipelineTriggers([pollSCM('* * * * *')])
		])
        // Below line triggers this job every minute 
		pipelineTriggers([pollSCM('* * * * *')]),
		parameters([choice(choices: [
			'dev1.lazizm.com', 
			'qa1.lazizm.com', 
			'stage1.lazizm.com', 
			'prod1.lazizm.com'], 
			description: 'Please choose an environment', 
			name: 'ENVIR')]), 
		])

    // Pulls repo from developer
	stage("Pull Repo"){
		git   'https://github.com/farrukh90/cool_website.git'
	}

	stage("Install Prerequisites"){
		sh """
		ssh centos@jenkins_worker1.lazizm.com                 sudo yum install httpd -y
		"""
	}
	   //Copies over developers files to different enviroment
	stage("Copy artifacts"){
		sh """
		scp -r *  centos@jenkins_worker1.lazizm.com:/tmp
		ssh centos@jenkins_worker1.lazizm.com                 sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@jenkins_worker1.lazizm.com                 sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@jenkins_worker1.lazizm.com				   sudo chown centos:centos /var/www/html/
		ssh centos@jenkins_worker1.lazizm.com				   sudo chmod 777 /var/www/html/*
		"""
	}
	    //Restart web server
	stage("Restart web server"){
		sh "ssh centos@jenkins_worker1.lazizm.com                 sudo systemctl restart httpd"
	}

	//Sends a message to slack 
	stage("Slack"){
		slackSend color: '#BADA55', message: 'Hello, World!'
	}
}
