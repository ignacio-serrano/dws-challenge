# dws-challenge

Notes
-----

  * An in-memory-only database is by no means production ready. All data is going to be lost whenever the service is 
  restarted. It has to be replaced by some persistent (and ideally distributed) data store for the service to be 
  horizontally scalable. Both Cassandra and MongoDB are rock solid and have support from Spring for an easy integration.
  * The current `AccountsRepositoryInMemory` is based on a `ConcurrentHashMap`. When an element is required by 
  `computeIfPresent` the whole bucket where it lives is locked. A storage that allows locking single elements could be
  more efficient. Also, I don't quite like the idea of having the logic of verifying the amount within the repository 
  class. It's business logic and belongs in a service class. However with the current `ConcurrentHashMap` storage it 
  is the _simplest possible approach that works_ that I could find.
  * The error response when there is a bean validation violation is not very human friendly. This could be solved by 
  adding an `@ExceptionHandler` for `ConstraintViolationException` to return the error rendered by the bean validator.
  * Other exceptions are not being logged. It is arguable whether they represent errors in the application as a whole or
  not. Without more context it is impossible to tell whether they require logging or not. Is this service publicly 
  exposed? Or is it just for internal use? Anyway, if deemed necessary, I would handle the exceptions with an 
  `@ExceptionHandler` in the controller instead of the current `@ResponseStatus` in the exceptions.