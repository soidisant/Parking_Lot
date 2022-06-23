package parking

import kotlin.system.exitProcess

class ParkingLot {

    companion object {
        val REGISTRATION_PATTERN = "[A-Za-z-0-9]+".toRegex()
        val COLOR_PATTERN = "[A-Za-z]+".toRegex()
    }

    private data class Spot(val number: Int, var registration: String?, var color: String?) {

        fun empty() = registration.isNullOrEmpty() && color.isNullOrEmpty()
        fun occupied() = !empty()

        fun vacate() {
            registration = null
            color = null
        }
    }

    private lateinit var spots: List<Spot>

    private fun initSpots(numberOfSpots: String) {
        numberOfSpots.toIntOrNull()?.takeIf { it > 0 }?.let { nb ->
            spots = List<Spot>(nb) {
                Spot(it + 1, null, null)
            }
        } ?: throw Exception("Invalid number of spots.")
    }

    private fun availableSpots() = spots.count { it.empty() }

    private fun park(registration: String, color: String): Spot {
        if (!registration.matches(REGISTRATION_PATTERN))
            throw Exception("Invalid registration number.")

        if (!color.matches(COLOR_PATTERN))
            throw Exception("Invalid color")

        if (availableSpots() == 0)
            throw Exception("Sorry, the parking lot is full.")

        return spots.first { it.empty() }.also { spot ->
            spot.registration = registration
            spot.color = color
        }
    }

    private fun leave(spotNumber: String) {
        spotNumber.toIntOrNull()?.let { number ->
            spots.firstOrNull { it.number == number }?.let {
                if (it.empty()) {
                    throw Exception("There is no car in spot ${it.number}.")
                }
                it.vacate()
                println("Spot ${it.number} is free.")
            }
        } ?: throw Exception("Invalid spot number")
    }

    private fun printStatus() {
        if (availableSpots() == spots.size) {
            println("Parking lot is empty.")
        } else
            spots.filter { it.occupied() }.forEach {
                println("${it.number} ${it.registration} ${it.color}")
            }
    }

    private fun by_color(color: String, tranfso: Spot.() -> Any?) {
        spots.filter { it.occupied() && it.color!!.matches(color.toRegex(RegexOption.IGNORE_CASE)) }.let {
            if (it.isEmpty()) {
                println("No cars with color $color were found.")
            } else {
                println(it.joinToString(", ") { it.tranfso().toString()!! })
            }
        }
    }

    private fun spot_by_reg(registration: String) {
        spots.firstOrNull() { it.occupied() && it.registration == registration }?.let {
            println(it.number)
        } ?: println("No cars with registration number $registration were found.")
    }

    fun open() {
        do {
            readln().let { input ->
                try {
                    if (input.equals("exit"))
                        exitProcess(0)
                    input.split(" ").let { items ->
                        if (items.size < 1) {
                            throw Exception("Wrong input")
                        }
                        items[0].let { command ->
                            when (command) {
                                "create" -> {
                                    if (items.size == 2) {
                                        initSpots(items[1])
                                        println("Created a parking lot with ${spots.size} spots.")
                                    } else
                                        throw Exception("Wrong arguments for command create")
                                }
                                else -> {
                                    if (!this::spots.isInitialized) {
                                        throw Exception("Sorry, a parking lot has not been created.")
                                    }
                                    when (command) {
                                        "park" -> {
                                            if (items.size == 3) {
                                                park(items[1], items[2]).let { spot ->
                                                    println("${spot.color} car parked in spot ${spot.number}.")
                                                }
                                            } else
                                                throw Exception("Wrong arguments for command park")
                                        }
                                        "leave" -> {
                                            if (items.size == 2) {
                                                leave(items[1])
                                            } else
                                                throw Exception("Wrong arguments for command park")
                                        }
                                        "reg_by_color" -> {
                                            if (items.size == 2) {
                                                by_color(items[1], (Spot::registration))
                                            } else
                                                throw Exception("Wrong arguments for command reg_by_color")
                                        }
                                        "spot_by_color" -> {
                                            if (items.size == 2) {
                                                by_color(items[1], (Spot::number))
                                            } else
                                                throw Exception("Wrong arguments for command spot_by_color")
                                        }
                                        "spot_by_reg" -> {
                                            if (items.size == 2) {
                                                spot_by_reg(items[1])
                                            } else
                                                throw Exception("Wrong arguments for command spot_by_reg")
                                        }
                                        "status" -> {
                                            printStatus()
                                        }
                                        else -> throw Exception("Unknown command")
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        } while (true)
    }
}

fun main() {
    ParkingLot().open()
}
