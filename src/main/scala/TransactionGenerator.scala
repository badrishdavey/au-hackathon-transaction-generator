import java.time.LocalDateTime

import com.google.gson.Gson
import java.io.FileInputStream
import java.time.format.DateTimeFormatter
import java.util.Properties

import scala.util.Random

object TransactionGenerator extends Detector {

  val rng = new Random()
  val gson: Gson = new Gson()

  var streamedTransactions: Set[StreamedTransaction] = Set()
  var streamedTransactionsVector: Vector[StreamedTransaction] = Vector()
  var accounts: Set[account] = Set()

  val fileProperties = new Properties()
  fileProperties.load(new FileInputStream("client.properties"))

  var count = 0

  {
    println("Reading from data file...")
    val bufferedSource = io.Source.fromFile(fileProperties.getProperty("data.input.file"))
    for (line <- bufferedSource.getLines()) {
      var jsonEntry = line
      if(jsonEntry.charAt(0)=='[')
        jsonEntry = jsonEntry.substring(1)
      if(jsonEntry.charAt(jsonEntry.length-1)==',' || jsonEntry.charAt(jsonEntry.length-1)==']')
        jsonEntry = jsonEntry.substring(0, jsonEntry.length-1)
      val acct = gson.fromJson(jsonEntry, classOf[account])
      accounts += acct
      for (cust <- acct.customer) {
        for(txn <- cust.transactions) {
          streamedTransactions += StreamedTransaction(
            acct.account_id,
            s"${cust.customer_id}${cust.transactions.length}",
            cust.customer_id,
            cust.first_name,
            cust.last_name,
            cust.zipcode,
            cust.gender,
            cust.is_married,
            cust.email,
            txn.amount,
            txn.merchant_name,
            txn.zipcode,
            acct.credit_card_type,
            cust.credit_card_number,
            null,
            txn.rewards_earned
          )
        }
      }
    }
    accounts = null
    streamedTransactionsVector = streamedTransactions.toVector
    streamedTransactions = null
    println("Finished loading data.")
  }

  override val authGenerator = (_: Any) => {
    val t = streamedTransactionsVector(count)
    count += 1
    if(count == streamedTransactionsVector.size) count = 0
    val date = LocalDateTime.now()
    t.transaction_id = (t.transaction_id.toLong + 1).toString
    t.tx_time = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    gson.toJson(t)
  }
}

case class StreamedTransaction(
                              account_id: String,
                              var transaction_id: String,
                              customer_id: String,
                              first_name: String,
                              last_name: String,
                              customer_zipcode: String,
                              gender: String,
                              is_married: String,
                              email: String,
                              amount: String,
                              merchant: String,
                              transaction_zipcode: String,
                              card_type: String,
                              card_number: String,
                              var tx_time: String,
                              rewards_earned: String
                              )

case class transaction(
                      amount: String,
                      country: String,
                      day: String,
                      month: String,
                      year: String,
                      merchant_name: String,
                      rewards_earned: String,
                      transaction_id: String,
                      transaction_row_id: String,
                      zipcode: String
                      )

case class customer(
                   country: String,
                   credit_card_number: String,
                   customer_id: String,
                   dob: String,
                   email: String,
                   first_name: String,
                   gender: String,
                   is_married: String,
                   is_primary: String,
                   last_name: String,
                   transactions: Array[transaction],
                   zipcode: String
                   )

case class account(
                  account_balance: String,
                  account_id: String,
                  account_row_id: String,
                  account_spend_limit: String,
                  credit_card_type: String,
                  customer: Array[customer],
                  payment: Array[payment]
                  )

case class merchant(
                   merchant_name: String,
                   amount: String,
                   country: String,
                   zipcode: String,
                   rewards_earned: String
                   )

case class payment(
                  date: String,
                  amount: String,
                  rewards_used: String
                  )