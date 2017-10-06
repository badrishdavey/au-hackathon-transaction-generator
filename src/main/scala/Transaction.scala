import java.util.Map
import java.util.HashMap

import collection.JavaConversions._


object Transaction {

  val accountId = "account_id"
  val transactionId = "transaction_id"
  val customerId = "customer_id"
  val firstName = "first_name"
  val lastName = "last_name"
  val customerZipCode = "customer_zipcode"
  val gender = "gender"
  val isMarried = "is_married"
  val email = "e_mail"
  val amount = "amount"
  val merchant = "merchant"
  val transactionZipCode = "transaction_zipcode"
  val cardType = "card_type"
  val cardNumber = "card_number"
  val date = "date"
  val rewardsEarned = "rewards_earned"


  val allFields = List(
  )

  val getBlank = () => {
    val ans = new HashMap[String, String]()
    allFields.foreach(ans.put(_, ""))
    ans
  }

  val getBlankWith = (fields: List[Tuple2[String, String]]) => {
    val ans = getBlank()
    fields.foreach((h: Tuple2[String, String]) => ans.put(h._1, h._2))
    ans
  }

  val getDefault = () => {
    val map = new HashMap[String, String]()
    List(
      (accountId, ""),
      (transactionId, ""),
      (customerId, ""),
      (firstName, ""),
      (lastName, ""),
      (customerZipCode, ""),
      (gender, ""),
      (isMarried, ""),
      (email, ""),
      (amount, ""),
      (merchant, ""),
      (transactionZipCode, ""),
      (cardType, ""),
      (cardNumber, ""),
      (date, ""),
      (rewardsEarned, "")
    ).foreach((h: Tuple2[String, String]) => map.put(h._1, h._2))
    map
  }

  val getDefaultWith = (fields: List[Tuple2[String, String]]) => {
    val ans = getDefault()
    fields.foreach((h: Tuple2[String, String]) => ans.put(h._1, h._2))
    ans
  }

  val toJson = (map: Map[String, String]) => {
    val str = new StringBuilder()
    for ((k, v) <- map) {
      str.append(", \"" + k + "\"" + ": " + "\"" + v + "\"")
    }
    "{" + str.toString.substring(2) + "}"
  }
}
