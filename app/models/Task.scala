package models

case class Task(id: Int, name: String)

object Task {
  private var taskList: List[Task] = List()
  private var lastId : Int = 0

  def all: List[Task] = taskList

  def add(taskName: String) = {
    val newId: Int = lastId + 1
    lastId = lastId + 1
    taskList = taskList ++ List(Task(newId, taskName))
  }

  def delete(taskId: Int) = {
    taskList = taskList.filterNot(task => task.id == taskId)
  }

}
