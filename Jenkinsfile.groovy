node {
  properties([parameters([string(defaultValue: 'IP', description: 'Where to build?', name: 'ENV', trim: true)])])
  stage("Clone repo"){
    git 'git@github.com:neshimbekova/Flaskex.git'
  }
  stage('Build application') {
    sh "scp -r * ec2-user@${ENV}:/tmp"
    sh "ssh ec2-user@${ENV} pip install -r /tmp/requirements.txt"

  }
  stage("App Run"){
    sh "ssh ec2-user@${ENV} python /tmp/app.py"
  }
}
