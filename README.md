the file contents go like: 
RailwayManagementSystem
  - Source Packages
    - railway
      - Main.java (or Main.class)
    - railway.db
      - DBConnection.class
      - PDFGenerator.class
    - railway.gui
      - BookingHistoryFrame.java
      - CancelBookingFrame.java
      - CancellationHistoryFrame.java
      - DashboardFrame.java
      - LoginFrame.java
      - RegisterFrame.java
  - Test Packages
  - Libraries
    - mysql-connector-j-9.3.0.jar
    - kernel-9.1.0.jar
    - layout-9.1.0.jar
    - io-9.1.0.jar
    - commons-9.1.0.jar
    - slf4j-api-2.0.9.jar
    - slf4j-simple-2.0.9.jar
    - JDK 23 (Default)
  - Test Libraries 


✅ User-Side Functionalities Completed

    1. User Registration and Login

        - Users can register with basic details.

        - Login system with validation and session handling.

    2. Train Booking

        - Book tickets by selecting train, date, source, destination, and number of seats.

        - Booking information saved in the database.

    3. Cancel Booked Tickets

        - Users can cancel previously booked tickets.

        - Ticket status updated in the database.

    4. View Booking History

        - Displays all active (non-cancelled) bookings made by the user.

    5. View Cancellation History

        - Shows the list of all cancelled tickets with details.

    6. PDF Generation of Booked Ticket

        - Automatically generates a PDF after booking.

        - Opens the ticket PDF after successful booking using system's default PDF viewer.

    7. Train Search with Filters

        - Filter available trains based on source and destination stations.

    8. Sorting Table Columns

        - Allows sorting of booking and train tables by clicking on column headers (e.g., by Train Name, Date).
