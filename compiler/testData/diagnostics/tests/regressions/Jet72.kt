// JET-72 Type inference doesn't work when iterating over ArrayList

import java.util.ArrayList

abstract class Item(val room: <!CLASS_HAS_KOTLIN_ANALOG!>Object<!>) {
   abstract val name : String
}

val items: ArrayList<Item> = ArrayList<Item>()

fun test(room : <!CLASS_HAS_KOTLIN_ANALOG!>Object<!>) {
  for(val item: Item? in items) {
    if (item?.room === room) {
      System.out.println("You see " + item?.name)
    }
  }
}