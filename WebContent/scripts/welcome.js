//////////////////////// Button events ////////////////////////////

$(document).ready(function () {
	RandomColors.randomize();

    $('.button').click(function () {
        RandomColors.randomize();
    });

    $("#reg-link").on('click', function () {
    	RandomColors.randomize();
        toggleLoginRegisterForms();
    });

    $('#signin-link').on('click', function () {
    	RandomColors.randomize();
        toggleLoginRegisterForms();
    });

    $('#forgot-password-link').on('click', function () {
        showPasswordRecovery();
    })

    $('#go-back').on('click', function () {
        hidePasswordRecovery();
    })

    $('#book-button').on('click', function () {
        Appointments.book();
    });

    ////////////// CSS  ///////////////
    $('select').css('color', 'white');

});


// ////////////////////// View Toggling ////////////////////////////

function toggleLoginRegisterForms() {
    $('#login-form').slideToggle('slow');
    $('#register-form').slideToggle('slow');
}

function hideLoginForm() {
    $('#login-form').slideUp('slow');
}

function showPasswordRecovery() {
    $('#login-form').slideUp('slow');
    $('#forgot-password-form').slideDown('slow');
}

function hidePasswordRecovery() {
    $('#login-form').slideDown('slow');
    $('#forgot-password-form').slideUp('slow');
}

function displayBookingForm() {
	hideWarnings(); 
	Calendar.initDay();
    $('#login-form').slideUp('slow');
    $('#register-form').slideUp('slow');
    $('#booking-form').slideDown('slow');
}

function displayConfirmMessage() {
    $('#booking-info-message').html("Booking confirmed").show();
}

function displayCancelMessage() {
    $('#booking-info-message').html("Booking canceled").show();
}

function displayRegConfirmMessage() {
    $('#reg-info-message').html("Registration confirmed").show();
}

function hideInfoMessage() {
    $('.info-message').hide();
}

function showEmailAlreadyRegisteredMessage() {
    $('#email-already-registered').show();
}

function showDatabaseErrorDialog() {
    alert("There was an error connecting to the server. Try again.")
}

function showWrongAccessCodeMessage() {
    $('#wrong-access-code').show();
}

function showPasswordsDontMatchMessage() {
    $('#passwords-dont-match').show();
}

function showNoEmailRegisteredMessage() {
    $('#no-email-found').show();
}

function showWrongPasswordOrEmailMessage() {
    $('#invalid-password-or-email').show();
}

function hideWarnings() {
    $('.error-message').hide();
}

