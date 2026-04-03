function doPost(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Sheet1");
  var data = JSON.parse(e.postData.contents);

  // 🔥 NEW DELETE LOGIC
  if (data.action === "delete") {
    var invoiceId = data.invoice;
    var rows = sheet.getDataRange().getValues();
    // Neeche se upar delete karte hain taaki index na bigde
    for (var i = rows.length - 1; i >= 1; i--) {
      if (rows[i][0].toString() == invoiceId.toString()) {
        sheet.deleteRow(i + 1);
      }
    }
    return ContentService.createTextOutput(JSON.stringify({status:"success", message:"Deleted"})).setMimeType(ContentService.MimeType.JSON);
  }

  // --- AAPKA EXISTING INSERT LOGIC ---
  var invoice = data.invoice;
  var date = data.date;
  var name = data.name;
  var phone = data.phone;
  var items = data.items;
  var subtotal = 0;
  items.forEach(function(item) { subtotal += Number(item.a); });
  var cgst = Math.round((subtotal * 0.025) * 100) / 100;
  var sgst = Math.round((subtotal * 0.025) * 100) / 100;
  var grandTotal = Math.round(subtotal + cgst + sgst);

  items.forEach(function(item, index) {
    if (index === items.length - 1) {
      sheet.appendRow([invoice, date, name, phone, item.n, item.q, item.r, item.a, subtotal, cgst, sgst, grandTotal]);
    } else {
      sheet.appendRow([invoice, date, name, phone, item.n, item.q, item.r, item.a, "", "", "", ""]);
    }
  });

  return ContentService.createTextOutput(JSON.stringify({status:"success"})).setMimeType(ContentService.MimeType.JSON);
}

function doGet() {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Sheet1");
  var lastRow = sheet.getLastRow();
  var nextInvoice = 1;
  if (lastRow > 1) {
    var lastInvoice = sheet.getRange(lastRow, 1).getValue();
    nextInvoice = Number(lastInvoice) + 1;
  }
  return ContentService.createTextOutput(nextInvoice.toString()).setMimeType(ContentService.MimeType.TEXT);
}