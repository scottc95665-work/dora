def hasSealedPrivilege = account.privileges.contains("Sealed")
def hasSensitivePrivilege = account.privileges.contains("Sensitive Persons")

def isSealed = {
    it?.sensitivity_indicator == 'R'
}

def isSensitive = {
    it?.sensitivity_indicator == 'S'
}

def referralHasSealedData = {
    it?.allegations?.find {
        if (isSealed(it.perpetrator) || isSealed(it.victim)) {
            return true // break
        }
        return false // keep looping
    }
}

def referralHasSensitiveData = {
    it?.allegations?.find {
        if (isSensitive(it.perpetrator) || isSensitive(it.victim)) {
            return true // break
        }
        return false // keep looping
    }
}

def caseHasSealedData = {
    return it?.access_limitation?.limited_access_code == 'R'
}

def caseHasSensitiveData = {
    return it?.access_limitation?.limited_access_code == 'S'
}

response.hits?.hits?.each {
    it._source?.cases = it._source?.cases?.findAll({
        caseHasSealedData(it) ? hasSealedPrivilege : (caseHasSensitiveData(it) ? hasSensitivePrivilege : true)
    })

    it._source?.referrals = it._source?.referrals?.findAll({
        referralHasSensitiveData(it) ? hasSensitivePrivilege : (referralHasSealedData(it) ? hasSealedPrivilege : true)
    })
}

response