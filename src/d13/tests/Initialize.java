package d13.tests;

import d13.dao.Cell;
import d13.dao.Gender;
import d13.dao.Location;
import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.dao.UserState;
import d13.notify.BackgroundNotificationManager;
import d13.util.HibernateUtil;

public class Initialize {


    public static void main(String[] args) throws Exception {

        HibernateUtil.beginTransaction();

        RuntimeOptions.setOption("maintenance", "0");
        RuntimeOptions.setOption(BackgroundNotificationManager.RT_ENABLE_NOTIFY, "1");
        RuntimeOptions.setOption("notify.smtp_auth", "1");
        RuntimeOptions.setOption("notify.smtp_tls", "0");
        RuntimeOptions.setOption("notify.smtp_ssl", "0");
        RuntimeOptions.setOption("notify.smtp_host", "mail.disorient.info");
        RuntimeOptions.setOption("notify.smtp_port", "25");
        RuntimeOptions.setOption("notify.smtp_user", "camp@disorient.info");
        RuntimeOptions.setOption("notify.smtp_password", "the7ages");
        RuntimeOptions.setOption("notify.mail_from", "camp@disorient.info");
        RuntimeOptions.setOption("notify.base_url", "http://camp.disorient.info");
        
        RuntimeOptions.setOption("dues.paypal_email", "dues@disorient.info");
        RuntimeOptions.setOption("dues.paypal_site", "https://www.paypal.com/cgi-bin/webscr");
        RuntimeOptions.setOption("dues.paypal_notify", "http://camp.disorient.info/pay/notify.jsp");
        RuntimeOptions.setOption("dues.paypal_cancel", "http://camp.disorient.info/pay/cancel.jsp");
        RuntimeOptions.setOption("dues.paypal_return", "http://camp.disorient.info/pay/success.jsp");

        if (User.findByEmail("jason@disorient.info") == null) {        
            User user = new User("jason@disorient.info");
            //user.setAdmin(true);
            //user.setAdmissions(true);
            user.setAdminComment("Owner; created during initialization.");
            user.setEmergencyContact("Art Cipriani, 412-655-5004");
            user.setGender(Gender.MALE);
            user.setLocation(Location.NYC);
            user.setPhone("412-721-8889");
            user.setPassword("the7ages");
            user.setPlayaName(null);
            user.setRealName("Jason Cipriani");
            user.setState(UserState.NEW_USER);
            HibernateUtil.getCurrentSession().persist(user);
        }
        
        if (Cell.findRoot() == null) {
            
            Cell root, cell, cell2, cell3;
           
            root = Cell.newRoot();
            
            //==============================================================
            
            cell = root.addCategory("Pre-Playa");
            
            cell2 = cell.addCell("NYC Container Load-In");
            cell2.setPeople(6);
            cell2.setDescription("Managing and loading of the NYC Container, which will occur approximately 2 weeks before Burning Man.  He/she should be comfortable packing equipment efficiently and be physically capable. Proper packing will allow for efficient unloading on the playa and a smooth Alpha Camp experience.");
            
            //==============================================================
    
            cell = root.addCategory("Alpha Team");
           
            cell2 = cell.addCell("Alpha Team General");
            cell2.setPeople(20);
            cell2.setDescription("The Alpha Team will be the first Disorienters to arrive on the playa, paving the way for the Disorient camp. General responsibilities include container unloading, core infrastructure set up, and general preparation for camp build. Plus, it’s a great opportunity to hang out on a pristine playa and watch a city build from nothing.");
            
            cell2 = cell.addCell("NYC Container");
            cell2.setPeople(6);
            cell2.setDescription("Overseeing and unpacking the NYC container on the playa. The members of this team need to have a good grasp of all the stuff in the container and of the camp layout, so direction can be given as to where the various pieces get placed to ensure a smooth and efficient camp build.");
            
            cell2 = cell.addCell("LA Truck");
            cell2.setPeople(4);
            cell2.setDescription("This team will be bringing over supplies from LA to Burning Man. The team lead will be responsible for coordinating rental and load in of the truck in LA, bringing the truck to playa, load in after the burn, and returning the truck to LA.");
            
            //==============================================================
    
            cell = root.addCategory("DPW");
            
            cell2 = cell.addCell("General Camp Build");
            cell2.setPeople(80);
            cell2.setDescription("This team makes the magic happen. Responsibilities include general camp build tasks, setting up our frontage, common spaces, and infrastructure, and basically transforming an empty square of desert into a little something we like to call Disorient.");
            
            cell2 = cell.addCell("Shade");
            cell2.setPeople(12);
            cell2.setDescription("Responsible for overseeing the building (and breaking down) of the shade structures and ensuring that they are accurately placed. Shade is critical to the health and happiness of Disorient campers. This is not a physically demanding task, and is a great way to meet other Disorient DOers.");
    
            cell2 = cell.addCategory("Frontage");
            
            cell3 = cell2.addCell("Frontage Build");
            cell3.setPeople(0);
            cell3.setDescription("Assist the Frontage leads in constructing (and breaking down) our camp frontage project. This is the public face of Disorient, and we’re going to rock it hard this year. Construction will involve general build, pallet rack assembly, and decor.");
            
            cell3 = cell2.addCell("Pornj Star Lounge");
            cell3.setPeople(0);
            cell3.setDescription("The Pornj Star Lounge is back, and this team is responsible for building it and breaking it down. The Lounge is a public space for everybody to relax in and enjoy.");
            
            cell2 = cell.addCategory("Water");
            
            cell3 = cell2.addCell("Shower Build");
            cell3.setPeople(4);
            cell3.setDescription("Dirty Disorienters need showers, and this team is responsible for building and breaking down our camp shower stalls. This team works closely with the Water lead to ensure efficient graywater management and a pleasant showering experience for all.");
           
            cell3 = cell2.addCell("Water Station Setup");
            cell3.setPeople(2);
            cell3.setDescription("Builds, sets up and maintains the cleaning area of the kitchen (setting up sinks with pumps, spray bottles, etc.), builds shade for cleaning area.");
            
            cell3 = cell2.addCell("Water Maintenance");
            cell3.setPeople(4);
            cell3.setDescription("Responsible for keeping all water stations (showers, kitchen, hq water) from getting muddy, maintence of the grey water tank, run/maintenance of the grey water pumps, etc.");
            
            cell2 = cell.addCategory("Kitchen");
           
            cell3 = cell2.addCell("Kitchen Build Team");
            cell3.setPeople(6);
            cell3.setDescription("Builds the kitchen elements and responsible for the layout of the the kitchen prep areas and utilities, including microwave, blender and the maintenance of kitchen supplies (bowls, trays, serving ware, etc.)");
    
            cell3 = cell2.addCell("Dining Area Build Team");
            cell3.setPeople(6);
            cell3.setDescription("");
    
            cell2 = cell.addCategory("Sound");
    
            cell3 = cell2.addCell("Build/Technical");
            cell3.setPeople(6);
            cell3.setDescription("Works with Sound lead on setting up the sound for the dome and maintaining it each evening.  Different shifts are for maintenance, all are responsible for assisting in the initial build and set up. Please consider volunteering for an audio tech shift below as well!");
    
            cell3 = cell2.addCell("Tech for Wednesday 9P-4A");
            cell3.setPeople(1);
            cell3.setDescription("Audio tech for Wednesday night. Must be familiar with the sound system.");
            
            cell3 = cell2.addCell("Tech for Thursday 9P-4A");
            cell3.setPeople(1);
            cell3.setDescription("Audio tech for Thursday night. Must be familiar with the sound system.");
            
            cell3 = cell2.addCell("Tech for Friday 9P-4A");
            cell3.setPeople(1);
            cell3.setDescription("Audio tech for Friday night. Must be familiar with the sound system.");
            
            cell3 = cell2.addCell("Tech for Saturday 9P-4A");
            cell3.setPeople(1);
            cell3.setDescription("Audio tech for Saturday night. Must be familiar with the sound system.");
            
            cell3 = cell2.addCell("Tech for Sunday 9P-4A");
            cell3.setPeople(1);
            cell3.setDescription("Audio tech for Sunday night. Must be familiar with the sound system.");
            
            cell2 = cell.addCell("Lighting");
            cell2.setPeople(3);
            cell2.setDescription("");
            
            //==============================================================
            
            cell = root.addCategory("LNT");
    
            cell2 = cell.addCell("Trash/Recycling");
            cell2.setPeople(2);
            cell2.setDescription("Collecting trash and recycling after meals and putting them in the appropriate locations. This is primarily kitchen/HQ trash. Public trash collection is *not* provided!");
            
            cell2 = cell.addCell("Urn");
            cell2.setPeople(8);
            cell2.setDescription("Responsible for setting up and maintaining the Urn, including tending the fire, disposing of ashes, burning trash, encouraging others to burn their burnables, etc.");
            
            cell2 = cell.addCell("Disengage");
            cell2.setPeople(2);
            cell2.setDescription("Perform, coordinate and oversee campwide moop and line sweeps, help ensure that our lot is moop free after disengage, get us a big ol' green square on the moop map.");
            
            //==============================================================
            
            cell = root.addCategory("Kitchen");
         
            cell2 = cell.addCell("Cleanup");
            cell2.setPeople(16);
            cell2.setDescription("");
    
            //==============================================================
            
            cell = root.addCategory("People");
    
            cell2 = cell.addCell("Camp HQ");
            cell2.setPeople(24);
            cell2.setDescription("Set up and maintain our camp headquarters.  Assist with placement and checking in and out campers, provide campers with information. Shifts will be assigned at check-in.");
    
            cell2 = cell.addCell("Placement");
            cell2.setPeople(4);
            cell2.setDescription("Place campers according to the camp layout.");
    
            cell2 = cell.addCategory("Love Ministry");
            
            cell3 = cell2.addCell("Love Ministry General");
            cell3.setPeople(12);
            cell3.setDescription("Give love, snacks, encouragement and drinks to campers and workers.  Make sure everyone is happy, hydrated and loved!");
            
            cell3 = cell2.addCell("Pornj Patrol");
            cell3.setPeople(0);
            cell3.setDescription("Uphold the Disorient way! Help keep camp morale up and spread the love. ");
    
            cell3 = cell2.addCell("Alpha Camp");
            cell3.setPeople(3);
            cell3.setDescription("Give lots of love, snacks and drinks to Alpha camp.  Make sure they are well taken care of and loved while they build camp for us!");
    
            cell3 = cell2.addCell("Disengage");
            cell3.setPeople(3);
            cell3.setDescription("Give lots of love, snacks and drinks to the Disengage crew.  Make sure they are well taken care of and loved while they break down the rest of camp for us!");
    
            //==============================================================
            
            cell = root.addCategory("Disengage");
                    
            cell2 = cell.addCell("Disengage General");
            cell2.setPeople(40);
            cell2.setDescription("");
    
            cell2 = cell.addCell("Personnel / Coordinator");
            cell2.setPeople(5);
            cell2.setDescription("");
    
            cell2 = cell.addCategory("Outputs");
            
            cell3 = cell2.addCell("NYC Container Load-In");
            cell3.setPeople(6);
            cell3.setDescription("");
    
            cell3 = cell2.addCell("LA Truck Load-In");
            cell3.setPeople(5);
            cell3.setDescription("");
    
            cell3 = cell2.addCell("Disorient Container Packing");
            cell3.setPeople(9);
            cell3.setDescription("");
    
            //==============================================================
            
            HibernateUtil.getCurrentSession().save(root);
            
        }
        
        HibernateUtil.commitTransaction();
        
    }

}
