import AllowancePackageApi from "~/api/AllowancePackageApi";
import { toast } from "react-toastify";

const AllowancePackageService = {
  getAllAllowancePackages: async (params) => {
    try {
      const response = await AllowancePackageApi.getAllAllowancePackages(params);
      return response;
    } catch (error) {
      toast.error("Error fetching allowance packages.");
      console.error("Error fetching allowance packages:", error);
      return null;
    }
  },

  getAllowancePackageById: async (id) => {
    try {
      const response = await AllowancePackageApi.getAllowancePackageById(id);
      return response.data;
    } catch (error) {
      toast.error("Error fetching allowance package details.");
      console.error("Error fetching allowance package details:", error);
      return null;
    }
  },

  createAllowancePackage: async (data) => {
    try {
      const response = await AllowancePackageApi.createAllowancePackage(data);
      toast.success("Allowance package created successfully!");
      return response.data;
    } catch (error) {
      toast.error("Error creating allowance package.");
      console.error("Error creating allowance package:", error);
      return null;
    }
  },

  updateAllowancePackage: async (id, data) => {
    try {
      const response = await AllowancePackageApi.updateAllowancePackage(id, data);
      toast.success("Allowance package updated successfully!");
      return response.data;
    } catch (error) {
      toast.error("Error updating allowance package.");
      console.error("Error updating allowance package:", error);
      return null;
    }
  },

  deleteAllowancePackage: async (id) => {
    try {
      await AllowancePackageApi.deleteAllowancePackage(id);
      toast.success("Allowance package deleted successfully!");
      return true;
    } catch (error) {
      toast.error("Error deleting allowance package.");
      console.error("Error deleting allowance package:", error);
      return false;
    }
  },
};

export default AllowancePackageService;