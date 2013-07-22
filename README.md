# Building

This project builds using SBT http://www.scala-sbt.org

# Build/Run commands

To generate an executable jar, execute:

    sbt assembly

To run the tests, and execute the main class, execute:

    sbt test run

# Limitations & Regrets

* Console input has not been implemented.
* An 8x10 matrix is somewhat small for a spreadsheet.
* Endless recursion could be a possibility as there is no recursion detection.
* Use of mutable data-structures make this project non-threadsafe.
* Assigning a Cell to every location in the matrix is wastefull.
* The use of Array might be wastefull, a more advanced data structure might be better.
* And many more.
