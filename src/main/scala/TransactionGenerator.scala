import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.google.gson.Gson

import scala.util.Random


object TransactionGenerator extends Detector {

  val rng = new RandomNumber()
  var merchants: Set[merchant] = Set()
  var accounts: Set[account] = Set()
  val gson: Gson = new Gson()
  var transactionId: Int = 1000000000

  {
    val bufferedSource = io.Source.fromFile("data.json")
    for (line <- bufferedSource.getLines()) {
      var jsonEntry = line
      if(jsonEntry.charAt(0)=='[')
        jsonEntry = jsonEntry.substring(1)
      if(jsonEntry.charAt(jsonEntry.length-1)==',' || jsonEntry.charAt(jsonEntry.length-1)==']')
        jsonEntry = jsonEntry.substring(0, jsonEntry.length-1)
      val acct = gson.fromJson(jsonEntry, classOf[account])
      accounts += acct
      for (customer <- acct.customer) {
        for(txn <- customer.transactions) {
          merchants += merchant(txn.merchant_name, txn.amount, txn.country, txn.zipcode)
        }
      }
    }
  }

  override val authGenerator = (_: Any) => {
    val a = accounts.toVector(rng.nextInt(accounts.size))
    val c = a.customer.apply(rng.nextInt(a.customer.size))
    val m = merchants.toVector(rng.nextInt(merchants.size))
    val date = LocalDateTime.now()
    var amount = ((math rint m.amount.toDouble) * 100)/100 + rng.nextInt(-50, 50)/100
    if(amount < 0)
      amount = 0
    transactionId += 1
    s"""{"account_id":"${a.account_id}","transaction_id":"$transactionId","customer_id":"${c.customer_id}","first_name":"${c.first_name}","last_name":"${c.last_name}","customer_zipcode":"${c.zipcode}","gender":"${c.gender}","is_married":"${c.is_married}","email":"${c.email}","amount":"$amount","merchant":"${m.merchant_name}","transaction_zipcode":"${m.zipcode}","card_type":"${a.credit_card_type}","card_number":"${c.credit_card_number}","date_year":"${date.getYear}","date_month":"${date.getMonth}","date_day":"${date.getDayOfMonth}","rewards_earned":"${(((math rint amount) * 0.02) * 100) / 100}"}"""
  }
}

class RandomNumber extends Random{
  val rng = new Random()

  def nextInt(lower: Int, upper: Int): Int = {
    if(lower > upper)
      nextInt(upper, lower)
    else
      lower + rng.nextInt(upper - lower + 1)
  }
}

case class transaction(
                      amount: String,
                      country: String,
                      date: String,
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
                   zipcode: String
                   )

case class payment(
                  date: String,
                  amount: String,
                  rewards_used: String
                  )