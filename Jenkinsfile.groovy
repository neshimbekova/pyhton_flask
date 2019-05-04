node {
  properties([parameters([string(defaultValue: 'IP', description: 'Where to build?', name: 'ENV', trim: true)])])
  stage("Clone repo"){
    git 'git@github.com:neshimbekova/Flaskex.git'
  }
  stage('Build application') {
    sh "ssh ec2-user@${ENV} sudo yum install python-pip -y"
    sh "ssh ec2-user@${ENV} sudo rm -rf /tmp/*"
  }
  stage("Copy flask"){
    sh "scp -r * ec2-user@${ENV}:/tmp"
  }
  stage("Create folder"){
    sh "ssh ec2-user@${ENV} sudo mkdir -p /flaskex"
  }
  // Block to work on next
  stage("Write to a file"){
    sh "ssh ec2-user@${ENV} echo [Unit] > /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo After=network.target >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo [Service] >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo Type=simple >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo ExecStart=/bin/python /flaskex/app.py  >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo Restart=on-abort >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo [Install] >> /tmp/flaskex.service"
        sh "ssh ec2-user@${ENV} echo WantedBy=multi-user.target >> /tmp/flaskex.service"
    }
    //Block to work on next

  stage("Copy to the system"){
    sh "ssh ec2-user@${ENV} sudo cp -r /tmp/flaskex.service  /etc/systemd/system"
  }
  stage("move files from tmp to /flaskex"){
    try {
    sh "ssh ec2-user@${ENV} sudo cp -r /tmp/* /flaskex"
  }
    catch(err){
      sh "echo did not copy"
    }
  stage("install requirements.txt"){
    sh "ssh ec2-user@${ENV} sudo pip install -r /tmp/requirements.txt"
  }
  stage("App Run"){
    sh "ssh ec2-user@${ENV} systemctl star flaskex"
  }
}
