package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Plan;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class PayPalBillingAgreement {

    private APIContext apiContext;

    @Autowired
    public PayPalBillingAgreement(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    /**
     * Returns date in ISO 8601 format
     * @param date Date java object
     * @return Date in ISO 8601 format
     */
    private static String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    /**
     * Define an agreement for paypal subscription
     * @param id Created plan id
     * @return Agreement object that can be used to create an agreement
     */
    public Agreement define(@NonNull String id) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();

        Agreement agreement = new Agreement();
        agreement.setName("Codebin agreement");
        agreement.setDescription("Codebin subscription agreement");
        agreement.setStartDate(getDate(date));

        Plan plan = new Plan();
        plan.setId(id);
        agreement.setPlan(plan);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        agreement.setPayer(payer);

        return agreement;
    }

    /**
     * Creates defined agreement which is passed in param
     * @param agreement Defined agreement
     * @return URL to be redirected
     * @throws PayPalRESTException When approval_url cannot be find
     * @throws MalformedURLException When URL to be redirected is malformed
     * @throws UnsupportedEncodingException When there is a unsupported encoding used
     */
    public String create(@NonNull Agreement agreement)
            throws PayPalRESTException, MalformedURLException, UnsupportedEncodingException {
        agreement = agreement.create(apiContext);

        for (Links link : agreement.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                return link.getHref();
            }
        }

        throw new PayPalRESTException("approval_url not found");
    }

    /**
     * Executes created agreement
     * @param token Token which is passed in request parameter after successfull redirection
     * @throws PayPalRESTException When agreement cannot be exeucted
     */
    public void execute(@NonNull String token) throws PayPalRESTException {
        Agreement agreement = new Agreement();
        agreement.setToken(token);

        Agreement.execute(apiContext, agreement.getToken());
    }
}
