package co.vine.ws

import co.vine.ws.exceptions.SystemError
import co.vine.ws.utility.TwitterDsl
import org.scalatest.FlatSpec

/**
 * Master test suite using Flat Spec
 */
class MasterTestSuite extends FlatSpec {

  "A User list creation " should "return a listId" in {
    assert(TwitterDsl.createUsersList() != null)
  }

  "Member addition process " should "add members to the newly created uesr list" in {
    var listId = TwitterDsl.createUsersList()
    assert(TwitterDsl.addMembersToList("PunyacheRau", listId) != null)
  }

  it should "produce 'Addition of members to the users list failed error' when Member addition method fails" in {
    intercept[SystemError] {
      TwitterDsl.addMembersToList("eqweqw", TwitterDsl.createUsersList())
    }
  }

  "List deletion process " should " delete the user list" in {
    TwitterDsl.deleteUsersList(TwitterDsl.createUsersList())
  }
  it should "produce 'Deletion of list failed error' when deleting list fails" in {
    intercept[SystemError] {
      TwitterDsl.deleteUsersList("112")
    }
  }

}
