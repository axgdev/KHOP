# Change Log for KHOP

### v0.51.1-alpha

* Fix `PlanObj` not creating a copy of the state

### v0.51.0

* Add more debug messages for iterative version of the planner
* Remove recursive version of the planner


### v0.50.0

First release of KHOP as a package with dependencies, changes to previous versions:

* Remove configuration to create JAR artifact in Intellij IDEA
* Add configuration to deploy to Artifactory
* Make `NetworkElement` carry the generic type of the state
* Add `OperatorGroup` to group all operators for a primitive task
* Make iterative version of the planner work with `OperatorGroup`
