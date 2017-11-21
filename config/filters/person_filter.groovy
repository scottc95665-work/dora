final int COUNTY_CODE_TO_ID_DELTA = 1067
final String STATE_OF_CALIFORNIA_COUNTY_CODE = "99"
final String STATE_OF_CALIFORNIA_COUNTY_ID = "1126"

final String authCountyId = STATE_OF_CALIFORNIA_COUNTY_CODE == account.countyCode ?
        STATE_OF_CALIFORNIA_COUNTY_ID :
        account.countyCode.toInteger() + COUNTY_CODE_TO_ID_DELTA

def hasSealedPrivilege = account.privileges.contains("Sealed")
def hasSensitivePrivilege = account.privileges.contains("Sensitive Persons")

//def sameClientCounty = {
//    it?.client_county?.id == authCountyId
//}
//
//def clientHasNoCounty = {
//    it?.client_county == null
//}

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

response.hits?.hits?.each {

//    hasSameClientCounty = sameClientCounty(it._source)
//    hasNoCounty = clientHasNoCounty(it._source)

    it._source?.referrals = it._source?.referrals?.findAll({
        referralHasSensitiveData(it) ? hasSensitivePrivilege: true
    })

    it._source?.referrals = it._source?.referrals?.findAll({
        referralHasSealedData(it) ? hasSealedPrivilege: true
    })
}

response