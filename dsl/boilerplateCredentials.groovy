import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
// Echo the parameters passed from the parameterized seed job
println("Printing parameters passed from the parameterized seed job:")
println("GIT_USERNAME: ${params.GIT_USERNAME}")
println("GIT_PASSWORD: ${params.GIT_PASSWORD}")

def credsId = 'git-credentials-id'
def description = 'GitLab Username and Password'
def username = params.GIT_USERNAME // Passed from the parameterized seed job
def password = params.GIT_PASSWORD // Passed from the parameterized seed job

// Check if credentials already exist
def existingCreds = null
CredentialsStore store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
existingCreds = store?.getCredentials(Domain.global())?.find { it.id == credsId }

if (existingCreds) {
    println("Credentials with ID ${credsId} already exist.")
    if(params.FORCE_UPDATE) {  // FORCE_UPDATE is another parameter you could set in the seed job
        println("Force updating the existing credentials.")
        existingCreds.username = username
        existingCreds.password = hudson.util.Secret.fromString(password)
        store.updateCredentials(Domain.global(), existingCreds, existingCreds)
    }
} else {
    println("Creating new credentials with ID ${credsId}.")
    def newCreds = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        credsId,
        description,
        username,
        password
    )
    store.addCredentials(Domain.global(), newCreds)
}

