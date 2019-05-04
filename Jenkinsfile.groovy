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
  stage("Copy to the system"){
    sh "ssh ec2-user@${ENV} sudo cp -r /tmp/flaskex.service  /etc/systemd/system"
  }
  stage("move files from tmp to /flaskex"){
    sh "ssh ec2-user@${ENV} sudo cp -r /tmp/* /flsakex"
  }
  stage("install requirements.txt"){
    sh "ssh ec2-user@${ENV} sudo pip install -r /tmp/requirements.txt"
  }
  stage("App Run"){
    sh "ssh ec2-user@${ENV} systemctl star flaskex"
  }
}
