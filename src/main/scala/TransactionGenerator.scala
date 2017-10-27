import java.time.LocalDateTime

import com.google.gson.Gson
import java.io.FileInputStream
import java.util.Properties

import scala.util.Random

object TransactionGenerator extends Detector {

  val rng = new Random()
  val gson: Gson = new Gson()

  var merchants: Set[merchant] = Set()
  var accounts: Set[account] = Set()

  val fileProperties = new Properties()
  fileProperties.load(new FileInputStream("client.properties"))

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
      for (customer <- acct.customer) {
        customer.transaction_length = customer.transactions.length
        for(txn <- customer.transactions) {
          merchants += merchant(txn.merchant_name, txn.amount, txn.country, txn.zipcode, txn.rewards_earned)
        }
      }
    }
    println("Finished loading data.")
  }

  override val authGenerator = (_: Any) => {
    val a = accounts.toVector(rng.nextInt(accounts.size))
    val c = a.customer.apply(rng.nextInt(a.customer.length))
    val m = merchants.toVector(rng.nextInt(merchants.size))
    val date = LocalDateTime.now()
    c.transaction_length += 1
    s"""{"account_id":"${a.account_id}","transaction_id":"${c.customer_id}${c.transaction_length % 1000}","customer_id":"${c.customer_id}","first_name":"${c.first_name}","last_name":"${c.last_name}","customer_zipcode":"${c.zipcode}","gender":"${c.gender}","is_married":"${c.is_married}","email":"${c.email}","amount":"${m.amount}","merchant":"${m.merchant_name}","transaction_zipcode":"${m.zipcode}","card_type":"${a.credit_card_type}","card_number":"${c.credit_card_number}","date_year":"${date.getYear}","date_month":"${date.getMonth}","date_day":"${date.getDayOfMonth}","time_hour":"${date.getHour}","time_minute":"${date.getMinute}","time_second":"${date.getSecond}","rewards_earned":"${m.rewards_earned}"}"""
  }
}

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
                   zipcode: String,
                   var transaction_length: Int = 0
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