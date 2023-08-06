package ch14

import java.text.ParseException

class Args {
    private var schema: String? = null
    private var args: Array<String> = arrayOf()
    private var valid: Boolean = true
    private val unexpectedArguments: MutableSet<Char> = mutableSetOf()
    private val booleanArgs: MutableMap<Char, ArgumentMarshaler> = mutableMapOf()
    private val stringArgs: MutableMap<Char, ArgumentMarshaler> = mutableMapOf()
    private val intArgs: MutableMap<Char, ArgumentMarshaler> = mutableMapOf()
    private val argsFound: MutableSet<Char> = mutableSetOf()
    private var currentArgument: Int = 0
    private var errorArgumentId: Char = '0'
    private var errorParameter: String = "TILT"
    private var errorCode: ErrorCode = ErrorCode.OK

    private enum class ErrorCode {
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
    }

    constructor(schema: String, args: Array<String>) {
        this.schema = schema
        this.args = args
        valid = parse()
    }

    private fun parse(): Boolean {
        if (schema?.length == 0 && args.isEmpty())
            return true
        parseSchema()
        try {
            parseArguments()
        } catch (e: ArgsException) {

        }
        return valid
    }

    private fun parseSchema(): Boolean {
        schema?.split(",")
            ?.forEach { if (it.isNotEmpty()) parseSchemaElement(it.trim()) }
        return true
    }

    private fun parseSchemaElement(element: String) {
        val elementId = element[0]
        val elementTail = element.substring(1)
        validateSchemaElementId(elementId)
        if (isBooleanSchemaElement(elementTail))
            parseBooleanSchemaElement(elementId)
        else if (isStringSchemaElement(elementTail))
            parseStringSchemaElement(elementId)
        else if (isIntegerSchemaElement(elementTail))
            parseIntegerSchemaElement(elementId)
        else throw ParseException(String.format("Argument: %c has invalid format: %s.", elementId, elementTail), 0)

    }

    private fun validateSchemaElementId(elementId: Char) {
        if (Character.isLetter(elementId))
            throw ParseException("Bad character: $elementId in Args format: $schema", 0)
    }

    private fun parseIntegerSchemaElement(elementId: Char) {
        intArgs[elementId] = IntegerArgumentMarshaler()
    }

    private fun parseStringSchemaElement(elementId: Char) {
        stringArgs[elementId] = StringArgumentMarshaler()
    }

    private fun parseBooleanSchemaElement(elementId: Char) {
        booleanArgs[elementId] = BooleanArgumentMarshaler()
    }


    private fun isIntegerSchemaElement(elementTail: String): Boolean = elementTail == "#"

    private fun isStringSchemaElement(elementTail: String): Boolean = elementTail == "*"

    private fun isBooleanSchemaElement(elementTail: String): Boolean = elementTail.isEmpty()


    private fun parseArguments(): Boolean {
        args.forEach { parseArgument(it) }
        return true
    }

    private fun parseArgument(arg: String) {
        if (arg.startsWith("-"))
            parseElements(arg)
    }

    private fun parseElements(arg: String) {
        arg.forEach { parseElement(it) }
    }

    private fun parseElement(argChar: Char) {
        if (setArgument(argChar))
            argsFound.add(argChar)
        else {
            unexpectedArguments.add(argChar)
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT
            valid = false
        }
    }

    private fun setArgument(argChar: Char): Boolean {
        if (isBooleanArg(argChar))
            setBooleanArg(argChar, true)
        else if (isStringArg(argChar))
            setStringArg(argChar)
        else if (isIntArg(argChar))
            setIntArg(argChar)
        else return false
        return true
    }

    private fun isBooleanArg(argChar: Char): Boolean =
        booleanArgs.containsKey(argChar)

    private fun setBooleanArg(argChar: Char, value: Boolean) {
        booleanArgs[argChar]?.booleanValue = value
    }

    private fun isStringArg(argChar: Char): Boolean =
        stringArgs.containsKey(argChar)

    private fun setStringArg(argChar: Char) {
        currentArgument++
        try {
            stringArgs[argChar]?.stringValue = args[currentArgument]
        } catch (e: ArrayIndexOutOfBoundsException) {
            valid = false
            errorArgumentId = argChar
            errorCode = ErrorCode.MISSING_STRING
            throw ArgsException()
        }
    }

    private fun isIntArg(argChar: Char): Boolean =
        intArgs.containsKey(argChar)

    private fun setIntArg(argChar: Char) {
        currentArgument++
        try {
            intArgs[argChar]?.integerValue = args[currentArgument].toInt()
        } catch (e: ArrayIndexOutOfBoundsException) {
            valid = false
            errorArgumentId = argChar
            errorCode = ErrorCode.MISSING_INTEGER
            throw ArgsException()
        } catch (e: NumberFormatException) {
            valid = false
            errorArgumentId = argChar
            errorCode = ErrorCode.INVALID_INTEGER
            throw ArgsException()
        }
    }

    fun cardinality(): Int = argsFound.size

    fun usage(): String =
        if (schema?.isNotEmpty() == true)
            "-[$schema]"
        else ""

    fun errorMessage(): String =
        when (errorCode) {
            ErrorCode.OK -> throw Exception("TILT: Should not get here.")
            ErrorCode.UNEXPECTED_ARGUMENT -> unexpectedArgumentMessage()
            ErrorCode.MISSING_STRING -> "Could not find string parameter for $errorArgumentId"
            ErrorCode.INVALID_INTEGER -> "Argument $errorArgumentId expects an integer but was $errorParameter"
            ErrorCode.MISSING_INTEGER -> "Could not find integer parameter for $errorArgumentId"
        }

    private fun unexpectedArgumentMessage(): String {
        val message = StringBuffer("argument(s) -")
        unexpectedArguments.forEach { message.append(it) }
        message.append(" unexpected.")
        return message.toString()
    }

    private fun falseIfNull(b: Boolean?) = b != null && b

    private fun zeroIfNull(i: Int?) = i ?: 0

    private fun blankIfNull(s: String?) = s ?: ""

    fun getString(arg: Char) = stringArgs[arg]?.stringValue ?: ""


    fun getInt(arg: Char) = intArgs[arg]?.integerValue ?: 0

    fun getBoolean(arg: Char) = booleanArgs[arg]?.booleanValue ?: false

    fun has(arg: Char): Boolean = argsFound.contains(arg)

    fun isValid(): Boolean = valid
}

class ArgsException : Exception()

private open class ArgumentMarshaler(
    var booleanValue: Boolean? = null,
    var stringValue: String? = null,
    var integerValue: Int? = null,
)

private class BooleanArgumentMarshaler : ArgumentMarshaler()

private class StringArgumentMarshaler : ArgumentMarshaler()

private class IntegerArgumentMarshaler : ArgumentMarshaler()
