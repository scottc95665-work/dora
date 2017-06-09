## JMeter Tests

JMeter Tests are part of the API project as jmx files eg. *API/src/test/resources/jmeter/CmsReferralLegacyNSComparison.jmx*

Some JMeter Tests are executed as part of Jenkins Continuous Integration after each build of the API project

The User Properties for JMeter are part of the api-deploy project

### CmsReferralLegacyNSComparison.jmx

This test compares a Referral created via the API New System to a Baseline Referral from the CMS Legacy System

The intent is that a referral created via the API New System should be identical to one with the same data created via the Legacy System

A baseline legacy sql is added to ci-seeds to provide a Baseline Referral to test against when this test is executed as part of Jenkins Continuous Integration

### Tests for individual resources

These test REST calls against the resource

_allegations.jmx
_clients.jmx
_cmsreferrals.jmx
_referralClients.jmx
_referrals.jmx
_reporters.jmx
_staffpersons.jmx
address_validation.jmx
addresses.jmx
autocomplete.jmx
people_search.jmx

### CmsDb2LoadTestIncludingSetUpAndTearDown.jmx

For this test The following properties are added to JMeter user.properties

load.test.number.of.users<br>
load.test.loops<br>
load.test.tabledata<br>
load.test.db2.url<br>
load.test.db2.user<br>
load.test.db2.password<br>

This test creates a load against the DB2 database. The data created as part of the test is cleared up when the test is complete. The tables covered include:

CLIENT_T<br>
REPTR_T<br>
COLTRL_T<br>
OTH_KIDT<br>
EDPRVCNT<br>
OTH_ADLT<br>
ATTRNY_T<br>
OCL_NM_T<br>
SB_PVDRT<br>
SVC_PVRT<br>
REFERL_T<br>
CLN_COLT<br>

