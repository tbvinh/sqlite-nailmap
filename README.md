# sqlite-nailmap
Import "Nail Salon Addresses - Cleaned June 11, 2020.xlsx" into db.sqlite
import "export-customer.xlsx" to exclude db2.sqlite2

"SQL Clause"
====


select tc.Company, ta.StreetAddress, tu.PlaceName, tu.StateCode, ta.IDUSZip
from tbCustomer tc
join tbAddress ta on ta.IDCustomer = tc.ID
join tbUSZIP tu on tu.ID= ta.IDUSZip
join tbLead tl on tl.IDCustomer = tc.ID
where tl.IDLeadStatus = 5
union
select tc.Company, ta.StreetAddress, tu.PlaceName, tu.StateCode, ta.IDUSZip
from tbCustomer tc
join tbAddress ta on ta.IDCustomer = tc.ID
join tbUSZIP tu on tu.ID= ta.IDUSZip
join tbOrder o on o.IDCustomer = tc.ID
where (o.IDOrderStatus >= 4 and o.IDOrderStatus < 9)
		or (o.IDOrderStatus = 10 and o.ShippedDate >= DATEADD(year, -1, getdate()))

====
