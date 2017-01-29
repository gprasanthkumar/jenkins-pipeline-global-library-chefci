package org.typo3.chefci.v2.stages

import org.typo3.chefci.helpers.JenkinsHelper
import org.typo3.chefci.helpers.Slack

class Lint extends AbstractStage {

    Lint(Object script, JenkinsHelper jenkinsHelper, Slack slack) {
        super(script, 'Lint', jenkinsHelper, slack)
    }

    @Override
    void execute() {
        script.stage(stageName) {
            foodcritic()
            rubocop()
        }
    }

    private def foodcritic(){
        script.node {
            // we have to manually disable these directories because foodcritic is stupid by default.
            // https://github.com/acrmp/foodcritic/issues/148
            script.sh('foodcritic . --exclude spec --exclude test')
        }
    }

    private def rubocop(){
        script.node {
            // see also http://atomic-penguin.github.io/blog/2014/04/29/stupid-jenkins-and-chef-tricks-part-1-rubocop/
            script.sh('rubocop --fail-level E')
            script.step([$class: 'WarningsPublisher', canComputeNew: false, canResolveRelativePaths: false, consoleParsers: [[parserName: 'Foodcritic'], [parserName: 'Rubocop']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', unHealthy: ''])
            script.step([$class: 'AnalysisPublisher'])
        }
    }

}