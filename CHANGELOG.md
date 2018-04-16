# Change Log for KHOP

### v0.53.1-alpha

* Add option to return the failed plan

### v0.52.1

* Add debug message when a task fails

### v0.52.0

* Add `PrimitiveTask` and `ComplexTask` classes

### v0.51.1

* Previous versions were tested and no other bug was found.
* Update Kotlin from 1.2.30 to 1.2.31

### v0.51.1-alpha.2

* Pass copy of initial network instead of reference also when the initial plans are empty.
* Replace the use of LinkedList to ArrayDeque for better performance.

### v0.51.1-alpha.1

* Fix passing reference of initial network to the iteration stack instead of a copy.

### v0.51.1-alpha

* Fix `PlanObj` not creating a copy of the state.

### v0.51.0

* Add more debug messages for iterative version of the planner.
* Remove recursive version of the planner.


### v0.50.0

First release of KHOP as a package with dependencies, changes to previous versions:

* Remove configuration to create JAR artifact in Intellij IDEA.
* Add configuration to deploy to Artifactory.
* Make `NetworkElement` carry the generic type of the state.
* Add `OperatorGroup` to group all operators for a primitive task.
* Make iterative version of the planner work with `OperatorGroup`.
