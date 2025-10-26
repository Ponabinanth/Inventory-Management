package org.example;
import org.example.dao.userDAOImpl;
import org.example.model.User;
import org.example.model.User.UserRole;
import org.example.service.EmailUtil;
import org.example.service.OTPService;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class App {

    private static final EmailUtil emailUtil = new EmailUtil();
    private static final OTPService otpService = new OTPService(emailUtil);
    private static final userDAOImpl userAuthDAO = new userDAOImpl();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        displayWelcomeScreen();
        mainMenu();
    }

    private static void displayWelcomeScreen() {
        String LINE_WIDTH = "═════════════════════════════════════════════════";
        System.out.println("╔" + LINE_WIDTH +  "╗");
        System.out.println("         📦 INVENTORY MANAGEMENT SYSTEM 📈       ");
        System.out.println("╚" + LINE_WIDTH +  "╝");
    }
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. 🔑 Login");
            System.out.println("2. 📝 Register");
            System.out.println("3. 📧 Verify Email (Simulated)");
            System.out.println("4. 🚪 Exit");
            System.out.print("🎯 Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    if (!login()) {
                    } else {
                        managerMenu();
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    verifyEmail();
                    break;
                case "4":
                    exit();
                    running = false;
                    break;
                default:
                    System.err.println("❌ Invalid choice. Please try again.");
            }
        }
    }
    private static boolean login() {
        System.out.println("\n--- Login ---");
        System.out.print("📧 Enter Email : ");
        String email = sc.nextLine();
        System.out.print("🔒 Enter Password: ");
        String password = sc.nextLine();

        User user = userAuthDAO.login(email, password);

        if (user == null) {
            System.err.println("❌ Invalid credentials. Access denied.");
            return false;
        }

        System.out.println("\n✅ Login successful. Welcome, " + user.getFullName() + " (" + user.getRole() + ")!");

        if (!userAuthDAO.isAuthorized(UserRole.MANAGER)) {
            System.err.println("🚫 Access denied. Only Managers can log in to the main system.");
            userAuthDAO.logout();
            return false;
        }

        if (!user.isVerified()) {
            handleUnverifiedUser(user);
            userAuthDAO.logout();
            return false;
        }

        return performOtpCheck(user);
    }

    private static boolean performOtpCheck(User user) {
        otpService.generateAndSendOTP(user.getUserId(), user.getEmail());
        System.out.print("📬 Enter the 6-digit OTP received: ");
        String enteredOTP = sc.nextLine();

        if (otpService.verifyOTP(user.getUserId(), enteredOTP)) {
            System.out.println("✅ OTP verified successfully. Entering system...");
            return true;
        } else {
            System.err.println("❌ OTP verification failed. Access denied.");
            return false;
        }
    }

    private static void handleUnverifiedUser(User user) {
        System.err.println("\n⚠️ Access Denied: Your email address is unverified.");

        System.out.println("   Please check your email (" + user.getEmail() + ") for a verification link.");

        System.out.print("   Would you like to resend the verification link? (Y/N): ");
        String choice = sc.nextLine().trim().toUpperCase();

        if (choice.equals("Y")) {
            System.out.println("   📧 Verification link resent. Please check your inbox.");
        }

        System.out.println("   You must verify your email before proceeding.");
    }
    private static void register() {
        System.out.println("\n--- User Registration ---");
        System.out.print("👤 Enter Full Name: ");
        String name = sc.nextLine();
        System.out.print("📧 Enter Email: ");
        String email = sc.nextLine();
        System.out.print("🔒 Choose Password: ");
        String password = sc.nextLine();

        User newUser = new User(
                UUID.randomUUID().toString(),
                name,
                email,
                password,
                UserRole.VIEWER
        );

        if (userAuthDAO.addUser(newUser)) {
            String token = UUID.randomUUID().toString();

            emailUtil.sendVerificationEmail(email, token);

            System.out.println("\n✅ Registration successful for " + name + ".");
            System.out.println("   A verification link has been sent to " + email + ".");
            System.out.println("   Please verify your email to enable full access.");
        } else {
            System.err.println("❌ Registration failed. Email may already be in use.");
        }
    }
    private static void verifyEmail() {
        System.out.println("\n--- Email Verification (Simulated) ---");
        System.out.print("📧 Enter Email to Verify: ");
        String email = sc.nextLine();
        System.out.print("🔑 Enter Verification Token (Simulated: enter 'VERIFY'): ");
        String token = sc.nextLine();

        Optional<User> user = userAuthDAO.getUserByEmail(email);

        if (user.isPresent() && token.equalsIgnoreCase("VERIFY")) {

            user.get().setVerified(true);

            if (userAuthDAO.updateUser(user.orElse(null))) {
                System.out.println("\n✅ Email " + email + " verified successfully! You can now log in.");
            } else {
                System.err.println("❌ Could not update user status in the DAO.");
            }
        } else {
            System.err.println("❌ Verification failed. Invalid email or token.");
        }
    }
    private static void exit() {
        System.out.println("\n👋 Thank you for using the system. Goodbye!");
        userAuthDAO.logout();
        sc.close();
    }


    private static void managerMenu() {
        System.out.println("\n🚀 Entering Manager System...");
        System.out.println("Current User: " + userAuthDAO.getCurrentUser().getFullName());
        System.out.println("Returning to Main Menu.");
    }
}
