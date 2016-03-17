import co.vine.ws.TwitterAPIBeforeAndAfterAll
import co.vine.ws.exceptions.SystemError
import com.github.scribejava.core.model.{OAuth1AccessToken, Verb, OAuthRequest}
import org.scalatest.FlatSpec
import co.vine.ws.utility.{OauthService, TwitterDsl}
/**
 * Created by dhavalkolapkar on 3/16/16.
 */
class MasterTestSuite extends FlatSpec with TwitterAPIBeforeAndAfterAll  {

  "A User list creation " should "return a listId" in {
    assert(TwitterDsl.createUsersList()!= null)
  }

  "Member addition process " should "add members to the newly created uesr list" in {
    var listId=TwitterDsl.createUsersList()
    assert(TwitterDsl.addMembersToList("PunyacheRau", listId)!=null)
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
