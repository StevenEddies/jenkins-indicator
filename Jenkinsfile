node {

	stage 'Checkout'
	checkout scm
	
	stage 'Build'
	computeVersion()
	runGradle('clean classes testClasses')
	
	stage 'Unit Test'
	runGradle('test')
	step([$class: 'JUnitResultArchiver', testResults: '**/build/reports/unit-test/TEST-*.xml'])
	if (testFailures()) return
	
//	stage 'Integration Test'
//	runGradle('integrationTest')
//	step([$class: 'JUnitResultArchiver', testResults: '**/build/reports/integration-test/TEST-*.xml'])
//	if (testFailures()) return
	
	stage 'Assemble'
	runGradle('assemble')
	
	if (isReleaseBranch()) {
		stage 'Publish'
		runGradle('publish')
		scmTag()
	}
	
	stage 'Metrics'
	runGradle('pitest')
	step([$class: 'PitPublisher', mutationStatsFile: '**/build/reports/pitest/mutations.xml'])
}

void computeVersion() {
	def releaseMatch = env.BRANCH_NAME =~ /^v(\d+\.\d+)$/
	def developmentMatch = env.BRANCH_NAME =~ /^dev(\d+\.\d+)$/
	def issueMatch = env.BRANCH_NAME =~ /^iss(\d+)$/
	
	def prefix
	if (releaseMatch) prefix = releaseMatch[0][1]
	if (developmentMatch) prefix = "${developmentMatch[0][1]}-dev"
	if (issueMatch) prefix = "0-iss${issueMatch[0][1]}"
	
	version = "${prefix}.${env.BUILD_NUMBER}"
}

boolean isReleaseBranch() {
	return (env.BRANCH_NAME =~ /^v(\d+\.\d+)$/) || (env.BRANCH_NAME =~ /^dev(\d+\.\d+)$/)
}

boolean testFailures() {
	return currentBuild.result != null
}

void scmTag() {
	sh 'git config user.email "steven@eddies.me.uk"'
	sh 'git config user.name "Jenkins CI"'

	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '250caf7a-0e9e-4e44-b8ba-a91544ca8f85', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
		sh 'set +x'
		sh "git tag -a v${version} -m \"Jenkins build published\""
		sh 'git push https://$GIT_USERNAME:$GIT_PASSWORD@github.com/StevenEddies/jenkins-indicator.git --tags'
	}
}

void runGradle(String tasks) {
	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '644d8845-ac90-4f97-a64e-3e840f3584ad', usernameVariable: 'ARCHIVA_USERNAME', passwordVariable: 'ARCHIVA_PASSWORD']]) {
		sh 'set +x'
		sh "./gradlew ${tasks} -PbuildVersion=${version} -ParchivaUsername=\$ARCHIVA_USERNAME -ParchivaPassword=\$ARCHIVA_PASSWORD"
	}
}
